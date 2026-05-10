package io.sketch.dsql.builder;

public interface Ordered<T> extends Statement {
    Limited<T> limit(int limit);
    Limited<T> limit(int offset, int limit);
}
