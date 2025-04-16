package com.demo.meta;

import io.github.jleblanc64.hibernate5.meta.BagProvider;
import io.github.jleblanc64.hibernate5.meta.MetaList;
import io.vavr.collection.HashSet;
import io.vavr.collection.Set;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public class MetaImplSet implements MetaList<Set> {
    @Override
    public Class<Set> monadClass() {
        return Set.class;
    }

    @Override
    public Set fromJava(Collection l) {
        return HashSet.ofAll(l);
    }

    @Override
    public java.util.List toJava(Set l) {
        return l.toJavaList();
    }

    @Override
    public BagProvider<? extends Set> bag() {
        return new BagProvider<PersistentBagImplSet>() {

            @Override
            public PersistentBagImplSet of(SharedSessionContractImplementor session) {
                return new PersistentBagImplSet(session);
            }

            @Override
            public PersistentBagImplSet of(SharedSessionContractImplementor session, Collection<?> collection) {
                return new PersistentBagImplSet(session, collection);
            }
        };
    }
}

