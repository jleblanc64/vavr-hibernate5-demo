package com.demo.functional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;

public class OptionF<T> implements IOptionF<T> {
    public List<T> l;

    public OptionF(Optional<T> o) {
        l = o == null || o.isEmpty() ? List.of() : List.of(o.get());
    }

    public OptionF(List<T> l) {
        checkSize(l);
        this.l = l;
    }

    public List<T> l() {
        return new ArrayList<>(l);
    }

    @Override
    public OptionF<T> o() {
        return new OptionF<>(l);
    }

    @Override
    public void clear() {
        l.clear();
    }

    public static void checkSize(List<?> l) {
        if (l != null && l.size() > 1)
            throw new RuntimeException("Not supported");
    }

    public Optional<T> opt() {
        return l == null || l.isEmpty() ? Optional.empty() : ofNullable(l.get(0));
    }

    public <U> OptionF<U> map(Function<T, U> f) {
        return new OptionF<>(opt().map(f));
    }

    public <U> OptionF<U> flatMap(Function<T, OptionF<U>> f) {
        return new OptionF<>(opt().flatMap(t -> f.apply(t).opt()));
    }

    public <U> OptionF<U> flatMapI(Function<T, IOptionF<U>> f) {
        return flatMap(t -> f.apply(t).o());
    }

    public <U> OptionF<U> flatMapO(Function<T, Optional<U>> f) {
        return new OptionF<>(opt().flatMap(f));
    }

    public <U> U fold(Function<T, U> f, U u) {
        return opt().map(f).orElse(u);
    }

    public <U> U foldGet(Function<T, U> f, Supplier<U> u) {
        return opt().map(f).orElseGet(u);
    }

    public T orElse(T t) {
        return opt().orElse(t);
    }

    public T orElseGet(Supplier<T> t) {
        return opt().orElseGet(t);
    }

    public T get() {
        return orElse(null);
    }

    public static <T> OptionF<T> o(T t) {
        return new OptionF<>(ofNullable(t));
    }

    public static <T> OptionF<T> emptyO() {
        return o(null);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof OptionF))
            return false;

        return Objects.equals(l, ((OptionF) o).l);
    }

    @Override
    public int hashCode() {
        if (l == null)
            return 0;

        return l.hashCode();
    }
}
