package com.demo.meta;

import io.vavr.PartialFunction;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.Iterator;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.*;

import static com.google.common.collect.Sets.newHashSet;

public class PersistentBagImplSet extends MyPersistentSet implements Set {
    public PersistentBagImplSet(SharedSessionContractImplementor session) {
        super(session);
    }

    public PersistentBagImplSet(SharedSessionContractImplementor session, Collection coll) {
        super(session, newHashSet(coll.iterator()));
    }

    @Override
    public java.util.Set toJavaSet() {
        return newHashSet((iteratorPriv()));
    }

    @Override
    public Set add(Object element) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set addAll(Iterable elements) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public int length() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set remove(Object element) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set removeAll(Iterable elements) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Iterator iterator() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set diff(Set that) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set intersect(Set that) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set union(Set that) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set distinct() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set distinctBy(Comparator comparator) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set drop(int n) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set dropRight(int n) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set dropUntil(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set dropWhile(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set filter(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set reject(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Object foldRight(Object zero, BiFunction f) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Iterator<? extends Set> grouped(int size) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean hasDefiniteSize() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Object head() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set init() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Option<? extends Set> initOption() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isTraversableAgain() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Object last() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set orElse(Iterable other) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Tuple2<? extends Set, ? extends Set> partition(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isAsync() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public boolean isLazy() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set peek(Consumer action) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public String stringPrefix() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set replace(Object currentElement, Object newElement) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set replaceAll(Object currentElement, Object newElement) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set retainAll(Iterable elements) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set scan(Object zero, BiFunction operation) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Iterator<? extends Set> sliding(int size) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Iterator<? extends Set> sliding(int size, int step) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Tuple2<? extends Set, ? extends Set> span(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set tail() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Option<? extends Set> tailOption() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set take(int n) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set takeRight(int n) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set takeUntil(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set takeWhile(Predicate predicate) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<Tuple2> zipWithIndex() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set zipWithIndex(BiFunction mapper) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<Tuple2> zipAll(Iterable that, Object thisElem, Object thatElem) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set zipWith(Iterable that, BiFunction mapper) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set<Tuple2> zip(Iterable that) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Tuple3<? extends Set, ? extends Set, ? extends Set> unzip3(Function unzipper) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Tuple2<? extends Set, ? extends Set> unzip(Function unzipper) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Iterator<? extends Set> slideBy(Function classifier) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set scanRight(Object zero, BiFunction operation) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set scanLeft(Object zero, BiFunction operation) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set orElse(Supplier supplier) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set map(Function mapper) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Map groupBy(Function classifier) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set flatMap(Function mapper) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set distinctBy(Function keyExtractor) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public Set collect(PartialFunction partialFunction) {
        throw new RuntimeException("Not implemented");
    }
}
