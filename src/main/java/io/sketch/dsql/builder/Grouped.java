package io.sketch.dsql.builder;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Grouped<T> extends Statement {

    Filtered<T> having(Consumer<GroupRef<T>> callback);

    // Subquery-wrapping select
    Projected<T> select(Function<GroupRef<T>, List<Object>> fn);

    Ordered<T> orderBy(Consumer<SelectRef<T>> callback);
    Limited<T> limit(int limit);
    Limited<T> limit(int offset, int limit);

    @SuppressWarnings("unchecked")
    Limited<T> union(Select<?>... others);
}
