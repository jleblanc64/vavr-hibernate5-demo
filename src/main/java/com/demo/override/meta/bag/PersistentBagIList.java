package com.demo.override.meta.bag;

import com.demo.override.duplicate.MyPersistentBag;
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
        return null;
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public List tail() {
        return null;
    }

    @Override
    public Object apply(Object o) {
        return null;
    }

    @Override
    public boolean isDefinedAt(Object value) {
        return false;
    }
}
