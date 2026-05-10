package io.sketch.dsql.builder;

import java.util.function.Consumer;

public interface Projected<T> extends Statement {
    Ordered<T> orderBy(Consumer<SelectRef<T>> callback);
    Limited<T> limit(int limit);
    Limited<T> limit(int offset, int limit);
}
