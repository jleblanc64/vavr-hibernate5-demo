package com.demo.override.meta;

import com.demo.override.meta.bag.BagProviderOpt;
import com.demo.override.meta.bag.IBagProvider;
import io.vavr.control.Option;

public class MetaOptionF implements MetaOption<Option> {
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

    @Override
    public IBagProvider<? extends Option> bag() {
        return new BagProviderOpt();
    }
}
