package com.demo.override.meta;

import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public interface BagProvider<Bag> {
    Bag of(SharedSessionContractImplementor session);

    Bag of(SharedSessionContractImplementor session, Collection<?> collection);
}
