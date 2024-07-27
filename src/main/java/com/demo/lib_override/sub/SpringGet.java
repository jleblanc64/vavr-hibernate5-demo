package com.demo.lib_override.sub;

import com.demo.functional.OptionF;
import com.demo.lib_override.ValueWrapper;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.GetterFieldImpl;

import java.lang.reflect.Field;
import java.util.Optional;

import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.OverrideLibs.mSelf;

public class SpringGet {
    public static void override() {
        mSelf(GetterFieldImpl.class, "get", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            return get((GetterFieldImpl) self, args[0]);
        });
    }

    @SneakyThrows
    public static ValueWrapper get(GetterFieldImpl g, Object owner) {
        var field = getRefl(g, "field", Field.class);
        if (field.getName().equals("name")) {
            var v = (Optional<String>) field.get(owner);
            return new ValueWrapper(new OptionF<>(v).get());
        }

        return null;
    }
}
