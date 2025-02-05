package com.demo.override.meta.bag;

import com.demo.override.duplicate.MyPersistentBag;
import io.vavr.PartialFunction;
import io.vavr.collection.List;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public class PersistentBagIList extends MyPersistentBag implements List {

    @Deprecated
    public PersistentBagIList(SharedSessionContractImplementor session) {
        super(session);
    }

    public PersistentBagIList(SharedSessionContractImplementor session, Collection coll) {
        super(session, coll);
    }

    @Override
    public Object head() {
        return get(0);
    }

    @Override
    public int length() {
        return size();
    }

    @Override
    public List tail() {
        return List.ofAll(bag).tail();
    }

    @Override
    public Object apply(Object o) {
        return ((PartialFunction) List.ofAll(bag)).apply(o);
    }

    @Override
    public boolean isDefinedAt(Object value) {
        return ((PartialFunction) List.ofAll(bag)).isDefinedAt(value);
    }
}
