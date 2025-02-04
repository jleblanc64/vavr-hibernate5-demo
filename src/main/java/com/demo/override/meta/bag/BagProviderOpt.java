package com.demo.override.meta.bag;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public class BagProviderOpt implements IBagProvider<PersistentBagIOption> {
    @Override
    public PersistentBagIOption of(SharedSessionContractImplementor session) {
        return new PersistentBagIOption(session);
    }

    @Override
    public PersistentBagIOption of(SharedSessionContractImplementor session, Collection collection) {
        return new PersistentBagIOption(session, collection);
    }
}
