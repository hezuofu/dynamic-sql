package io.sketch.dsql.builder;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Filtered<T> extends Statement {

    Filtered<T> where(Consumer<T> callback);
    Grouped<T> groupBy(Function<T, GroupKeys> fn);
    Ordered<T> orderBy(Consumer<SelectRef<T>> callback);
    Limited<T> limit(int limit);
    Limited<T> limit(int offset, int limit);

    @SuppressWarnings("unchecked")
    Limited<T> union(Select<?>... others);
}
