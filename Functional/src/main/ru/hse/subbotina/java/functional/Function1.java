package ru.hse.subbotina.java.functional;

import org.jetbrains.annotations.NotNull;

public interface Function1<X, S> {
    S apply(X var);

    default <Y> Function1<X, Y> compose(@NotNull Function1<S, ? extends Y> prev) {
        return var -> prev.apply(Function1.this.apply(var));
    }
}
