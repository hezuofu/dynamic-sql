package io.sketch.dsql.builder;

public interface SelectRef<T> {
    Field<T, ?> col(String alias);
}
