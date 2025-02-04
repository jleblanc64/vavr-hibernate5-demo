package com.demo.override.meta.bag;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public class BagProviderList implements IBagProvider<PersistentBagIList> {
    @Override
    public PersistentBagIList of(SharedSessionContractImplementor session) {
        return new PersistentBagIList(session);
    }

    @Override
    public PersistentBagIList of(SharedSessionContractImplementor session, Collection collection) {
        return new PersistentBagIList(session, collection);
    }
}
