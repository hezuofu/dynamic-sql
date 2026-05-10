package io.sketch.dsql.builder;

public interface Insert<T> extends Statement {
    Insert<T> columns(Field<T,?>... cols);
    Insert<T> values(Object... vals);
    Insert<T> row(Object... vals);
    Insert<T> select(Select<?> subquery);
}
