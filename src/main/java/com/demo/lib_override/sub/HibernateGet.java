package com.demo.lib_override.sub;

import com.demo.functional.OptionF;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.GetterFieldImpl;

import java.lang.reflect.Field;
import java.util.Optional;

import static com.demo.lib_override.FieldMocked.getRefl;

public class HibernateGet {
    public static void override() {
        LibCustom.overrideWithSelf(GetterFieldImpl.class, "get", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            return get((GetterFieldImpl) self, args[0]);
        });
    }

    @SneakyThrows
    public static Object get(GetterFieldImpl g, Object owner) {
        var field = getRefl(g, "field", Field.class);
        if (field.getName().equals("name")) {
            var v = (Optional<String>) field.get(owner);
            return new OptionF<>(v).get();
        }

        return LibCustom.ORIGINAL;
    }
}
