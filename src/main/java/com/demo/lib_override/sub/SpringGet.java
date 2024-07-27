package com.demo.lib_override.sub;

import com.demo.functional.OptionF;
import com.demo.lib_override.ValueWrapper;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.GetterFieldImpl;

import java.lang.reflect.Field;

import static com.demo.functional.Functor.print;
import static com.demo.lib_override.FieldMocked.getRefl;
import static com.demo.lib_override.OverrideLibs.mSelf;

public class SpringGet {
    public static void override() {
        mSelf(GetterFieldImpl.class, "get", argsS -> {
            var args = argsS.args;
            var self = argsS.self;

            return getOver((GetterFieldImpl) self, args[0]);
        });
    }

    @SneakyThrows
    public static ValueWrapper getOver(GetterFieldImpl g, Object owner) {
        var field = (Field) getRefl(g, "field");
        print("get " + field);

        if (field.equals("")) {
            var v = field.get(owner);
            if (v instanceof OptionF)
                return new ValueWrapper(((OptionF) v).l());
        }

        return null;
    }
}
