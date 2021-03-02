package ru.hse.subbotina.java.collection;

import ru.hse.subbotina.java.functional.Function1;
import ru.hse.subbotina.java.functional.Function2;
import ru.hse.subbotina.java.predicate.Predicate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Collections {
    public static <X, S> List<S> map(Function1<X, ? extends S> f, Iterable<? extends X> a){
        List<S> result = new ArrayList<>();
        for (X x : a) {
            result.add(f.apply(x));
        }
        return result;
    }

    public static <X> List<X> filter(Predicate<X> p, Iterable<? extends X> a) {
        List<X> result = new ArrayList<>();
        for (X x : a) {
            if (p.apply(x)) {
                result.add(x);
            }
        }
        return result;
    }

    public static <X> List<X> takeWhile(Predicate<X> p, Iterable<? extends X> a) {
        List<X> result = new ArrayList<>();
        for (X x : a) {
            if (!p.apply(x)) {
                break;
            } else {
                result.add(x);
            }
        }
        return result;
    }

    public static <X> List<X> takeUnless(Predicate<X> p, Iterable<? extends X> a) {
        return takeWhile(p.not(), a);
    }

    private static <Y, X> Y toFold(Iterator<? extends X> iter, Function2<X, Y, ? extends Y> fun, Y init) {
        if (iter.hasNext()) {
            X elem = iter.next();
            Y toApply = toFold(iter, fun, init);
            return fun.apply(elem, toApply);
        }
        return init;
    }

    public static <X, Y> Y foldr(Function2<X, Y, ? extends Y> fun, Y init, Iterable<? extends X> iter) {
        return toFold(iter.iterator(), fun, init);
    }

    public static <X, Y> Y foldl(Function2<Y, X, ? extends Y> fun, Y init, Iterable<? extends X> iter) {
        Function1<Y, Y> id = x -> x;
        Function1<Y, Y> asFoldr = foldr((f, g) -> x -> g.apply(fun.apply(x, f)), id, iter);
        return asFoldr.apply(init);
    }
}
