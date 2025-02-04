package com.demo.override.hibernate;

import com.demo.override.FieldMocked;
import com.demo.override.duplicate.MyPersistentBag;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.SetterFieldImpl;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.demo.override.FieldMocked.getRefl;
import static com.demo.override.hibernate.OverrideConverter.extractManyToOneIOptionF;
import static com.demo.override.hibernate.OverrideConverter.isEntity;

public class OverrideSetterSet {
    public static void overrideSetterSet(MetaOption metaOption) {
        LibCustom.overrideWithSelf(SetterFieldImpl.class, "set", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            return setOver((SetterFieldImpl) self, args[0], args[1], metaOption);
        });
    }

    @SneakyThrows
    public static Object setOver(SetterFieldImpl s, Object target, Object value, MetaOption metaOption) {
        var field = (Field) getRefl(s, "field");

        if (extractManyToOneIOptionF(field.getAnnotations(), field.getGenericType(), metaOption) != null) {
            field.set(target, metaOption.fromValue(value));
            return null;
        }

        if (!metaOption.isSuperClassOf(field.getType()))
            return LibCustom.ORIGINAL;

        var typeParam = FieldMocked.paramClass(field.getGenericType().getTypeName());
        if (isEntity(typeParam.getDeclaredAnnotations()) && isEntity(target.getClass().getDeclaredAnnotations())) {
            if (value instanceof MyPersistentBag && metaOption.isSuperClassOf(value)) {
                field.set(target, value);
                return null;
            }

            if (value instanceof Collection) {
                var coll = (Collection) value;
                field.set(target, metaOption.fromList((List) coll.stream().collect(Collectors.toList())));
                return null;
            }
        }

        return LibCustom.ORIGINAL;
    }
}
