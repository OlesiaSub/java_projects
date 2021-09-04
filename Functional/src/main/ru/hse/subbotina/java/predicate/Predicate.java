package ru.hse.subbotina.java.predicate;

import org.jetbrains.annotations.NotNull;
import ru.hse.subbotina.java.functional.Function1;

public interface Predicate<X> extends Function1<X, Boolean> {

    default Predicate<X> or(@NotNull Predicate<X> prev) {
        return var -> Predicate.this.apply(var) || prev.apply(var);
    }

    default Predicate<X> and(@NotNull Predicate<X> prev) {
        return var -> Predicate.this.apply(var) && prev.apply(var);
    }

    default Predicate<X> not() {
        return var -> !Predicate.this.apply(var);
    }

    static <X> Predicate<X> ALWAYS_TRUE() {
        return var -> true;
    }

    static <X> Predicate<X> ALWAYS_FALSE() {
        return var -> false;
    }
}
