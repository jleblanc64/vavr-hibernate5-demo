package com.demo.implem;

import io.github.jleblanc64.libcustom.meta.BagProvider;
import io.github.jleblanc64.libcustom.meta.MetaList;
import io.vavr.collection.List;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public class MetaListImpl implements MetaList<List> {
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
    public BagProvider<? extends List> bag() {
        return new BagProvider<PersistentBagImpl>() {

            @Override
            public PersistentBagImpl of(SharedSessionContractImplementor session) {
                return new PersistentBagImpl(session);
            }

            @Override
            public PersistentBagImpl of(SharedSessionContractImplementor session, Collection<?> collection) {
                return new PersistentBagImpl(session, collection);
            }
        };
    }
}
