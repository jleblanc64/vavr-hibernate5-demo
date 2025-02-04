package com.demo.override.hibernate;

import com.demo.override.FieldMocked;
import com.demo.override.duplicate.MyPersistentBag;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.GetterFieldImpl;

import java.lang.reflect.Field;

import static com.demo.override.FieldMocked.getRefl;
import static com.demo.override.hibernate.OverrideConverter.extractManyToOneIOptionF;
import static com.demo.override.hibernate.OverrideConverter.isEntity;

public class OverrideGetterGet {
    public static void overrideGetterGet(MetaOption metaOption) {
        LibCustom.overrideWithSelf(GetterFieldImpl.class, "get", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            return getOver((GetterFieldImpl) self, args[0], metaOption);
        });
    }

    @SneakyThrows
    public static Object getOver(GetterFieldImpl g, Object owner, MetaOption metaOption) {
        var field = (Field) getRefl(g, "field");

        if (extractManyToOneIOptionF(field.getAnnotations(), field.getGenericType(), metaOption) != null) {
            var o = field.get(owner);
            return metaOption.getOrNull(o);
        }

        if (!metaOption.isSuperClassOf(field.getType()))
            return LibCustom.ORIGINAL;

        var typeParam = FieldMocked.paramClass(field.getGenericType().getTypeName());
        if (isEntity(typeParam.getDeclaredAnnotations()) && isEntity(owner.getClass().getDeclaredAnnotations())) {
            var value = field.get(owner);
            if (value instanceof MyPersistentBag)
                return value;

            if (metaOption.isSuperClassOf(value))
                return metaOption.asList(value);
        }

        return LibCustom.ORIGINAL;
    }
}
