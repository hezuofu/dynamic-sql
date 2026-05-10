package io.sketch.dsql.builder;

public interface GroupRef<T> {
    GroupTable<T> group();
    Field<T, ?> key1();
    Field<T, ?> key2();
    Field<T, ?> key3();
}
