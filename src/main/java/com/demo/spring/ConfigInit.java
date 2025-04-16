package com.demo.spring;

import com.demo.meta.MetaImplSet;
import io.github.jleblanc64.hibernate5.hibernate.VavrHibernate5;
import io.github.jleblanc64.hibernate5.hibernate.duplicate.JavaXProperty;
import io.github.jleblanc64.hibernate5.meta.MetaList;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.PropertyInferredData;
import org.hibernate.cfg.annotations.BagBinder;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.cfg.annotations.SetBinder;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.SetType;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import javax.persistence.metamodel.PluralAttribute;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static io.github.jleblanc64.hibernate5.hibernate.Utils.getRefl;
import static io.github.jleblanc64.hibernate5.hibernate.Utils.isOfType;

public class ConfigInit implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        var metaSet = new MetaImplSet();
        overrideCustom(metaSet);

        VavrHibernate5.override();
    }

    @SneakyThrows
    private static void overrideCustom(MetaList metaList) {
        var bagProvList = metaList.bag();

        LibCustom.modifyArg(org.hibernate.cfg.AnnotationBinder.class, "processElementAnnotations", 2, args -> {
            var pid = (PropertyInferredData) args[2];
            var p = pid.getProperty();
            var type = (Type) getRefl(p, "type");
            var at = (AccessType) getRefl(pid, "defaultAccess");
            var rm = (ReflectionManager) getRefl(pid, "reflectionManager");
            var j = JavaXProperty.of((JavaXMember) p, type, metaList);

            if (!(type instanceof ParameterizedType))
                return LibCustom.ORIGINAL;

            var rawType = ((ParameterizedType) type).getRawType();
            if (metaList.isSuperClassOf(rawType)) {
                var f = (Field) j.getMember();
                var jOver = JavaXProperty.of(f, type, j, metaList);
                return new PropertyInferredData(pid.getDeclaringClass(), jOver, at.getType(), rm);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.override(org.hibernate.metamodel.internal.AttributeFactory.class, "determineCollectionType", args -> {
            var clazz = (Class) args[0];
            if (metaList.isSuperClassOf(clazz))
                return isSet(metaList) ? PluralAttribute.CollectionType.SET : PluralAttribute.CollectionType.LIST;

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder.class, "build", x -> {
            var self = x.self;

            var collectionClass = (Class) getRefl(self, "collectionClass");
            var listAttrClass = Class.forName("org.hibernate.metamodel.model.domain.internal.ListAttributeImpl");
            if (isSet(metaList))
                listAttrClass = Class.forName("org.hibernate.metamodel.model.domain.internal.SetAttributeImpl");

            var constructor = listAttrClass.getDeclaredConstructor(PluralAttributeBuilder.class);
            constructor.setAccessible(true);

            if (metaList.isSuperClassOf(collectionClass))
                return constructor.newInstance(self);

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyArg(CollectionType.class, "getElementsIterator", 0, args -> {
            var collection = args[0];
            if (metaList.isSuperClassOf(collection))
                return metaList.toJava(collection);

            return LibCustom.ORIGINAL;
        });

        LibCustom.override(CollectionBinder.class, "getBinderFromBasicCollectionType", args -> {
            var bag = isSet(metaList) ? new SetBinder(false) : new BagBinder();
            return metaList.isSuperClassOf(args[0]) ? bag : LibCustom.ORIGINAL;
        });

        LibCustom.override(SetType.class, "instantiate", args -> {
            if (args.length == 1)
                return LibCustom.ORIGINAL;

            var pers = (AbstractCollectionPersister) args[1];
            if (isOfType(pers, metaList))
                return checkPersistentBag(bagProvList.of((SharedSessionContractImplementor) args[0]));

            return LibCustom.ORIGINAL;
        });

        LibCustom.override(SetType.class, "wrap", args -> {
            var arg1 = args[1];

            if (metaList.isSuperClassOf(arg1)) {
                var c = metaList.toJava(arg1);
                return checkPersistentBag(bagProvList.of((SharedSessionContractImplementor) args[0], c));
            }

            return LibCustom.ORIGINAL;
        });
    }

    static boolean isSet(MetaList meta) {
        return meta.monadClass().toString().toLowerCase().contains("set");
    }

    private static Class PERSISTENT_COLLECTION_CLASS = AbstractPersistentCollection.class;

    @SneakyThrows
    static Object checkPersistentBag(Object o) {
        if (o == null)
            return o;

        if (!PERSISTENT_COLLECTION_CLASS.isAssignableFrom(o.getClass()))
            throw new RuntimeException("Output of IBagProvider implem must extend " + PERSISTENT_COLLECTION_CLASS.getName());

        return o;
    }
}
