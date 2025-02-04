package com.demo.override.meta;

import com.demo.override.meta.bag.IBagProvider;

public interface WithClass<T> {
    Class<T> monadClass();

    IBagProvider<? extends T> bag();

    default boolean isSuperClassOf(Object o) {
        if (o == null)
            return false;

        var clazz = o instanceof Class ? ((Class) o) : o.getClass();
        return monadClass().isAssignableFrom(clazz);
    }
}
