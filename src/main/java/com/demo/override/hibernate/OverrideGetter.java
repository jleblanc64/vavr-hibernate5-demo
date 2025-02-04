package com.demo.override.hibernate;

import com.demo.override.FieldMocked;
import com.demo.override.meta.MetaOption;
import io.github.jleblanc64.libcustom.LibCustom;
import lombok.SneakyThrows;
import org.hibernate.property.access.spi.GetterFieldImpl;

import java.lang.reflect.Field;

import static com.demo.override.FieldMocked.getRefl;
import static com.demo.override.FieldMocked.typeArgsIOptionF;

public class OverrideGetter {
    public static void overrideGetter(MetaOption metaOption) {
        LibCustom.overrideWithSelf(GetterFieldImpl.class, "getMember", argsS -> {
            var self = argsS.self;
            return getMemberOver((GetterFieldImpl) self, metaOption);
        });
    }

    @SneakyThrows
    public static Object getMemberOver(GetterFieldImpl g, MetaOption metaOption) {
        var field = (Field) getRefl(g, "field");

        var typeArgs = typeArgsIOptionF(field, metaOption);
        if (typeArgs != null)
            return FieldMocked.getIOptionF(field, metaOption);

        return LibCustom.ORIGINAL;
    }
}
