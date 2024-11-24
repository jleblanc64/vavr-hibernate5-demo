package com.demo.lib_override.sub;

import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.SetterFieldImpl;

import java.lang.reflect.Field;

import static com.demo.functional.OptionF.o;
import static com.demo.lib_override.FieldMocked.getRefl;

public class HibernateSet {
    public static void override() {
        LibCustom.overrideWithSelf(SetterFieldImpl.class, "set", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            return set((SetterFieldImpl) self, args[0], args[1]);
        });
    }

    @SneakyThrows
    public static Object set(SetterFieldImpl s, Object target, Object value) {
        var field = getRefl(s, "field", Field.class);
        if (field.getName().equals("name")) {
            field.set(target, o(value).opt());
            return null;
        }

        return LibCustom.ORIGINAL;
    }
}
