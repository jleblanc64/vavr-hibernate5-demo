package com.demo.override;

import com.demo.override.duplicate.MyCollectionType;
import com.demo.override.duplicate.MyPersistentBag;
import com.demo.override.meta.MetaList;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.functional.ListF;
import lombok.SneakyThrows;
import org.hibernate.cfg.annotations.BagBinder;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.metamodel.internal.AttributeFactory;
import org.hibernate.metamodel.model.domain.internal.PluralAttributeBuilder;
import org.hibernate.persister.collection.OneToManyPersister;
import org.hibernate.type.BagType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptorRegistry;

import javax.persistence.metamodel.PluralAttribute;
import java.beans.Introspector;
import java.util.Collection;
import java.util.Map;

import static com.demo.override.FieldMocked.getRefl;
import static com.demo.override.hibernate.OverrideConverter.overrideConverter;
import static com.demo.override.hibernate.OverrideGetter.overrideGetter;
import static com.demo.override.hibernate.OverrideGetterGet.overrideGetterGet;
import static com.demo.override.hibernate.OverrideSetterSet.overrideSetterSet;

public class OverrideHibernate {
    static Class PERSISTENT_COLLECTION_CLASS = MyPersistentBag.class;

    @SneakyThrows
    public static void override(ListF<Class<?>> models, MetaOption metaOption, MetaList metaList) {
        var bagProvList = metaList.bag();
        var bagProvOpt = metaOption.bag();

        LibCustom.override(CollectionBinder.class, "getBinderFromBasicCollectionType", args -> {
            var clazz = (Class<?>) args[0];
            return metaList.isSuperClassOf(clazz) || metaOption.isSuperClassOf(clazz) ? new BagBinder() : LibCustom.ORIGINAL;
        });

        LibCustom.override(BagType.class, "instantiate", args -> {
            if (args.length == 1)
                return LibCustom.ORIGINAL;

            var bagProv = bagProvList;
            if (args[1] instanceof OneToManyPersister) {
                var persister = (OneToManyPersister) args[1];
                var path = persister.getNavigableRole().getFullPath();
                var i = path.lastIndexOf(".");
                var className = path.substring(0, i);
                var fieldName = path.substring(i + 1);

                var field = Class.forName(className).getDeclaredField(fieldName);
                if (metaOption.isSuperClassOf(field.getType()))
                    bagProv = bagProvOpt;
            }

            return checkPersistentBag(bagProv.of((SharedSessionContractImplementor) args[0]));
        });

        LibCustom.override(BagType.class, "wrap", args -> {
            var arg1 = args[1];

            var c = metaOption.isSuperClassOf(arg1) ? metaOption.asList(arg1) : (Collection) arg1;

            var bagProv = bagProvList;
            if (metaOption.isSuperClassOf(arg1))
                bagProv = bagProvOpt;

            return checkPersistentBag(bagProv.of((SharedSessionContractImplementor) args[0], c));
        });

        LibCustom.override(JavaTypeDescriptorRegistry.class, "getDescriptor", args ->
                metaOption.isSuperClassOf(args[0]) ? metaOption.hibernateDescriptor() : LibCustom.ORIGINAL);

        LibCustom.modifyArg(Class.forName("org.hibernate.type.CollectionType"), "getElementsIterator", 0, args -> {
            var collection = args[0];
            if (metaOption.isSuperClassOf(collection))
                return metaOption.asList(collection);

            return collection;
        });

        LibCustom.overrideWithSelf(CollectionType.class, "replaceElements", x -> {
            var args = x.args;
            var c = (CollectionType) x.self;

            return MyCollectionType.replaceElements(args[0], args[1], args[2], (Map) args[3], (SharedSessionContractImplementor) args[4], c);
        });

        LibCustom.override(AttributeFactory.class, "determineCollectionType", args -> {
            var clazz = (Class) args[0];
            if (metaOption.isSuperClassOf(clazz) || metaList.isSuperClassOf(clazz))
                return PluralAttribute.CollectionType.LIST;

            return LibCustom.ORIGINAL;
        });

        LibCustom.overrideWithSelf(PluralAttributeBuilder.class, "build", x -> {
            var self = x.self;

            var clazz = (Class) getRefl(self, "collectionClass");
            var listAttrClass = Class.forName("org.hibernate.metamodel.model.domain.internal.ListAttributeImpl");
            var constructor = listAttrClass.getDeclaredConstructor(PluralAttributeBuilder.class);
            constructor.setAccessible(true);

            if (metaOption.isSuperClassOf(clazz) || metaList.isSuperClassOf(clazz))
                return constructor.newInstance(self);

            return LibCustom.ORIGINAL;
        });

        // in hibernate models, replace null with empty Option
        for (var model : models)
            for (var p : Introspector.getBeanInfo(model).getPropertyDescriptors()) {

                var m = p.getReadMethod();
                if (m != null && metaOption.isSuperClassOf(m.getReturnType()))
                    LibCustom.modifyReturn(model, m.getName(), argsR -> {
                        var returned = argsR.returned;
                        return returned == null ? metaOption.fromValue(null) : returned;
                    });
            }

        overrideConverter(metaOption, metaList);
        overrideGetter(metaOption);
        overrideGetterGet(metaOption);
        overrideSetterSet(metaOption);
    }

    @SneakyThrows
    static Object checkPersistentBag(Object o) {
        if (o == null)
            return o;

        if (!PERSISTENT_COLLECTION_CLASS.isAssignableFrom(o.getClass()))
            throw new RuntimeException("Output of IBagProvider implem must extend " + PERSISTENT_COLLECTION_CLASS.getName());

        return o;
    }
}
