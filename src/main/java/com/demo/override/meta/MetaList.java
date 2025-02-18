package com.demo.override.meta;

import java.util.List;

public interface MetaList<T> extends WithClass<T> {
    T fromJava(List l);

    List toJava(T t);

    BagProvider<? extends T> bag();
}
