package com.demo.override.meta;

import com.demo.override.meta.bag.BagProviderList;
import com.demo.override.meta.bag.IBagProvider;
import io.vavr.collection.List;

public class MetaListF implements MetaList<List> {
    @Override
    public Class<List> monadClass() {
        return List.class;
    }

    @Override
    public List fromJava(java.util.List l) {
        return List.ofAll(l);
    }

    @Override
    public java.util.List toJava(List l) {
        return l.asJava();
    }

    @Override
    public IBagProvider<? extends List> bag() {
        return new BagProviderList();
    }
}
