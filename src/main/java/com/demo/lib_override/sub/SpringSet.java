package com.demo.lib_override.sub;

import com.demo.lib_override.ValueWrapper;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.SetterFieldImpl;

import java.lang.reflect.Field;

import static com.demo.functional.OptionF.o;
import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.OverrideLibs.mSelf;

public class SpringSet {
    public static void override() {
        mSelf(SetterFieldImpl.class, "set", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            return set((SetterFieldImpl) self, args[0], args[1]);
        });
    }

    @SneakyThrows
    public static Object set(SetterFieldImpl s, Object target, Object value) {
        var field = (Field) getRefl(s, "field");
        if (field.getName().equals("name")) {
            field.set(target, o(value).opt());
            return new ValueWrapper(null);
        }

        return null;
    }
}
