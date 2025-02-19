package com.demo.override.jackson;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;
import io.github.jleblanc64.libcustom.meta.MetaOption;

public class OptionSer<T> extends StdDelegatingSerializer {
    public OptionSer(MetaOption<T> metaOption) {
        super(new StdConverter<T, Object>() {
            @Override
            public Object convert(T value) {
                return metaOption.getOrNull(value);
            }
        });
    }

    @Override
    protected StdDelegatingSerializer withDelegate(Converter<Object, ?> c, JavaType t, JsonSerializer<?> deser) {
        return new StdDelegatingSerializer(c, t, deser);
    }
}

