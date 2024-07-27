package com.demo.functional;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.demo.functional.ListF.f;

public interface IOptionF<T> {
    OptionF<T> o();

    void clear();

    default <U> OptionF<U> map(Function<T, U> f) {
        return new OptionF<>(o().opt().map(f));
    }

    default <U> OptionF<U> flatMap(Function<T, OptionF<U>> f) {
        return new OptionF<>(o().opt().flatMap(t -> f.apply(t).opt()));
    }

    default <U> OptionF<U> flatMapI(Function<T, IOptionF<U>> f) {
        return new OptionF<>(o().opt().flatMap(t -> f.apply(t).o().opt()));
    }

    default <U> OptionF<U> flatMapO(Function<T, Optional<U>> f) {
        return new OptionF<>(o().opt().flatMap(f));
    }

    default void forEachF(Functor.ThrowingConsumer<T> f) {
        f(o().l).forEachF(f);
    }

    default <U> U fold(Function<T, U> f, U u) {
        return o().opt().map(f).orElse(u);
    }

    default <U> U foldGet(Function<T, U> f, Supplier<U> u) {
        return o().opt().map(f).orElseGet(u);
    }

    default void foldVoid(Functor.ThrowingConsumer<T> f, Functor.ThrowingRunnable u) {
        Optional<T> opt = o().opt();
        if (opt.isPresent())
            f.accept(opt.get());
        else
            u.run();
    }

    default T orElse(T t) {
        return o().opt().orElse(t);
    }

    default T orElseGet(Functor.ThrowingSupplier<T> t) {
        return o().opt().orElseGet(t);
    }

    default T orElseThrow() {
        return o().opt().orElseThrow();
    }

    default <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return o().opt().orElseThrow(exceptionSupplier);
    }

    default T get() {
        return o().get();
    }
}
