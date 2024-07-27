package com.demo.functional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.demo.functional.ListF.f;
import static java.util.Optional.ofNullable;

public interface IListF<T> extends List<T> {
    default <U> ListF<U> map(Functor.ThrowingFunction<T, U> fun) {
        return f(stream().map(fun).collect(Collectors.toList()));
    }

    default <U> Set<U> mapS(Functor.ThrowingFunction<T, U> f) {
        return new HashSet<>(map(f));
    }

    default <U> ListF<U> mapIdx(BiFunction<T, Integer, U> f) {
        List<T> l = copy();
        List<U> result = new ArrayList<>();
        for (int i = 0; i < l.size(); i++)
            result.add(f.apply(l.get(i), i));

        return f(result);
    }

    default <U> ListF<U> mapFilter(Functor.ThrowingFunction<T, OptionF<U>> fun) {
        return map(fun).filter(o -> o.opt().isPresent()).map(OptionF::get);
    }

    default <U> ListF<U> flatMapL(Functor.ThrowingFunction<T, List<U>> fun) {
        return f(stream().flatMap(x -> fun.apply(x).stream()).collect(Collectors.toList()));
    }

    default <U> ListF<U> flatMap(Functor.ThrowingFunction<T, Collection<U>> fun) {
        return f(stream().flatMap(x -> fun.apply(x).stream()).collect(Collectors.toList()));
    }

    default <U> ListF<U> flatMapIdx(BiFunction<T, Integer, Collection<U>> f) {
        List<T> l = copy();
        List<U> result = new ArrayList<>();
        for (int i = 0; i < l.size(); i++)
            result.addAll(f.apply(l.get(i), i));

        return f(result);
    }

    default void forEachIdx(BiConsumer<T, Integer> f) {
        List<T> l = copy();
        for (int i = 0; i < l.size(); i++)
            f.accept(l.get(i), i);
    }

    default void forEachIdx(Functor.ThrowingBiConsumer<T, Integer> f) {
        List<T> l = copy();
        for (int i = 0; i < l.size(); i++)
            f.accept(l.get(i), i);
    }

    default T find(Predicate<T> p) {
        return stream().filter(p).findFirst().get();
    }

    default T findSafe(Predicate<T> p) {
        return stream().filter(p).findFirst().orElse(null);
    }

    default Optional<T> findOpt(Predicate<T> p) {
        return ofNullable(findSafe(p));
    }

    default OptionF<T> findOptF(Predicate<T> p) {
        return new OptionF<>(findOpt(p));
    }

    default T getSafe(int i) {
        return i < size() ? get(i) : null;
    }

    default ListF<T> filter(Predicate<T> fun) {
        return f(stream().filter(fun).collect(Collectors.toList()));
    }

    default ListF<T> unNull() {
        return filter(t -> t != null);
    }

    default ListF<T> unique() {
        return f(new LinkedHashSet<>(copy()));
    }

    default boolean anyMatch(Predicate<T> p) {
        return stream().anyMatch(p);
    }

    default boolean allMatch(Predicate<T> p) {
        return stream().allMatch(p);
    }

    default ListF<T> sortF(Comparator<T> comp) {
        return f(stream().sorted(comp).collect(Collectors.toList()));
    }

    default ListF<T> sorted() {
        return f(stream().sorted().collect(Collectors.toList()));
    }

    default Set<T> toS() {
        return stream().collect(Collectors.toSet());
    }

    default ListF<T> copy() {
        return f(stream().collect(Collectors.toList()));
    }
}
