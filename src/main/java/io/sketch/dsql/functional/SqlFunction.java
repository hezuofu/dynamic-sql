package io.sketch.dsql.functional;

import java.util.function.Function;

@FunctionalInterface
public interface SqlFunction<T, R> extends Function<T, R> {

    default <V> SqlFunction<V, R> compose(SqlFunction<? super V, ? extends T> before) {
        return v -> apply(before.apply(v));
    }

    default <V> SqlFunction<T, V> andThen(SqlFunction<? super R, ? extends V> after) {
        return t -> after.apply(apply(t));
    }

    static <T> SqlFunction<T, T> identity() {
        return t -> t;
    }
}