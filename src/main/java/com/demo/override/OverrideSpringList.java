package com.demo.override;

import io.github.jleblanc64.libcustom.LibCustom;
import io.github.jleblanc64.libcustom.meta.MetaList;
import lombok.SneakyThrows;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.GenericConversionService;

import java.util.ArrayList;
import java.util.Collection;

public class OverrideSpringList {
    @SneakyThrows
    public static void override(MetaList metaList) {
        LibCustom.override(GenericConversionService.class, "convert", args -> {
            if (args == null || args.length != 3)
                return LibCustom.ORIGINAL;

            if (!(args[2] instanceof TypeDescriptor))
                return LibCustom.ORIGINAL;

            var targetType = (TypeDescriptor) args[2];
            if (!metaList.isSuperClassOf(targetType.getObjectType()))
                return LibCustom.ORIGINAL;

            var source = args[0];
            if (!(source instanceof Collection))
                return LibCustom.ORIGINAL;

            return metaList.fromJava(new ArrayList<>((Collection) source));
        });
    }
}
