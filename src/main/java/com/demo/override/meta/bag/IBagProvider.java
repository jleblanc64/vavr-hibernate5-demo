package com.demo.override.meta.bag;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public interface IBagProvider<Bag> {
    Bag of(SharedSessionContractImplementor session);

    Bag of(SharedSessionContractImplementor session, Collection collection);
}
