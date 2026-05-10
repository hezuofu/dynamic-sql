package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlResult;

import java.util.function.Consumer;

public interface Projected<T> extends Statement {
    Projected<T> orderBy(Consumer<SelectRef<T>> callback);
    Projected<T> limit(int limit);
    Projected<T> limit(int offset, int limit);
    @SuppressWarnings("unchecked")
    Projected<T> union(Select<T>... others);
    @SuppressWarnings("unchecked")
    Projected<T> unionAll(Select<T>... others);
    SqlResult toList();
}
