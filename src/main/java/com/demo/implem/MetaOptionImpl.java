package com.demo.implem;

import io.github.jleblanc64.libcustom.meta.MetaOption;
import io.vavr.control.Option;

public class MetaOptionImpl implements MetaOption<Option> {
    @Override
    public Class<Option> monadClass() {
        return Option.class;
    }

    @Override
    public Option<?> fromValue(Object v) {
        return Option.of(v);
    }

    @Override
    public Object getOrNull(Object o) {
        if (o == null)
            return null;

        return ((Option) o).getOrNull();
    }
}
