package ru.hse.subbotina.java.functional;

import org.jetbrains.annotations.NotNull;

public interface Function2 <X, Y, S> {
    S apply(X firstVar, Y secondVar);

    default <Z> Function2<X, Y, Z> compose(@NotNull Function1<S, ? extends Z> prev) {
        return (firstVar, secondVar) -> prev.apply(apply(firstVar, secondVar));
    }

    default Function1<Y, S> bind1(X firstVar) {
        return secondVar -> apply(firstVar, secondVar);
    }

    default Function1<X, S> bind2(Y secondVar) {
        return firstVar -> apply(firstVar, secondVar);
    }

    default  Function1<X, S> curry(Y secondVar) {
        return bind2(secondVar);
    }
}
