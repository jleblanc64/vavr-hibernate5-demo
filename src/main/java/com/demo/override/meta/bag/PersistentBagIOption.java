package com.demo.override.meta.bag;

import com.demo.override.duplicate.MyPersistentBag;
import io.vavr.control.Option;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;

public class PersistentBagIOption extends MyPersistentBag implements Option {

    @Deprecated
    public PersistentBagIOption(SharedSessionContractImplementor session) {
        super(session);
    }

    public PersistentBagIOption(SharedSessionContractImplementor session, Collection coll) {
        super(session, coll);
    }

    @Override
    public Object get() {
        if (iteratorPriv().hasNext())
            return iteratorPriv().next();

        return null;
    }

    @Override
    public String stringPrefix() {
        return null;
    }
}
