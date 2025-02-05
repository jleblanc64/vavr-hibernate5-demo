package com.demo.override;

import com.demo.override.duplicate.JavaXProperty;
import com.demo.override.duplicate.MyCollectionType;
import com.demo.override.meta.MetaList;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXMember;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.PropertyInferredData;
import org.hibernate.cfg.annotations.BagBinder;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.BagType;
import org.hibernate.type.CollectionType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import static com.demo.override.FieldMocked.getRefl;
import static com.demo.override.Utils.checkPersistentBag;
import static com.demo.override.Utils.isOfType;

public class OverrideHibernate {
    @SneakyThrows
    public static void override(MetaList metaList, MetaOption metaOption, io.github.jleblanc64.libcustom.functional.ListF<Class<?>> models) {
        var bagProvList = metaList.bag();

        LibCustom.modifyArg(org.hibernate.cfg.AnnotationBinder.class, "processElementAnnotations", 2, args -> {
            var pid = (PropertyInferredData) args[2];
            var p = pid.getProperty();
            var type = (Type) getRefl(p, "type");
            var at = (AccessType) getRefl(pid, "defaultAccess");
            var rm = (ReflectionManager) getRefl(pid, "reflectionManager");
            var j = JavaXProperty.of((JavaXMember) p, type, metaOption, metaList);

            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == io.vavr.collection.List.class) {
                var f = (java.lang.reflect.Field) j.getMember();
                var jOver = JavaXProperty.of(f, type, j, metaOption, metaList);
                return new PropertyInferredData(pid.getDeclaringClass(), jOver, at.getType(), rm);
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.override(org.hibernate.metamodel.internal.AttributeFactory.class, "determineCollectionType", args -> {
            var clazz = (Class) args[0];
            if (metaList.isSuperClassOf(clazz))
                return javax.persistence.metamodel.PluralAttribute.CollectionType.LIST;

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder.class, "build", x -> {
            var self = x.self;

            var collectionClass = (Class) getRefl(self, "collectionClass");
            var listAttrClass = Class.forName("org.hibernate.metamodel.model.domain.internal.ListAttributeImpl");
            var constructor = listAttrClass.getDeclaredConstructor(org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder.class);
            constructor.setAccessible(true);

            if (metaList.isSuperClassOf(collectionClass))
                return constructor.newInstance(self);

            return LibCustom.ORIGINAL;
        });

        LibCustom.modifyArg(Class.forName("org.hibernate.type.CollectionType"), "getElementsIterator", 0, args -> {
            var collection = args[0];
            if (metaList.isSuperClassOf(collection))
                return metaList.toJava(collection);

            return collection;
        });

        // in hibernate models, replace null with empty Option
        for (var model : models)
            for (var p : java.beans.Introspector.getBeanInfo(model).getPropertyDescriptors()) {

                var m = p.getReadMethod();
                if (m != null && metaOption.isSuperClassOf(m.getReturnType()))
                    LibCustom.modifyReturn(model, m.getName(), argsR -> {
                        var returned = argsR.returned;
                        return returned == null ? metaOption.fromValue(null) : returned;
                    });
            }

        LibCustom.override(CollectionBinder.class, "getBinderFromBasicCollectionType", args ->
                metaList.isSuperClassOf(args[0]) ? new BagBinder() : LibCustom.ORIGINAL);

        LibCustom.override(BagType.class, "instantiate", args -> {
            if (args.length == 1)
                return LibCustom.ORIGINAL;

            var pers = (org.hibernate.persister.collection.AbstractCollectionPersister) args[1];
            if (isOfType(pers, metaList))
                return checkPersistentBag(bagProvList.of((SharedSessionContractImplementor) args[0]));

            return LibCustom.ORIGINAL;
        });

        LibCustom.override(BagType.class, "wrap", args -> {
            var arg1 = args[1];

            if (metaList.isSuperClassOf(arg1)) {
                var c = metaList.toJava(arg1);
                return checkPersistentBag(bagProvList.of((SharedSessionContractImplementor) args[0], c));
            }

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(CollectionType.class, "replaceElements", x -> {
            var args = x.args;
            var c = (CollectionType) x.self;

            return MyCollectionType.replaceElements(args[0], args[1], args[2], (Map) args[3], (SharedSessionContractImplementor) args[4], c);
        });
    }
}
