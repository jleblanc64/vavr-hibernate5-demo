package com.demo.implem;

import com.sympheny.app.hibernate.override.duplicate.MyPersistentBag;
import io.vavr.PartialFunction;
import io.vavr.collection.List;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public class PersistentBagImpl extends MyPersistentBag implements io.vavr.collection.List {
    public PersistentBagImpl(SharedSessionContractImplementor session) {
        super(session);
    }

    public PersistentBagImpl(SharedSessionContractImplementor session, Collection coll) {
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
