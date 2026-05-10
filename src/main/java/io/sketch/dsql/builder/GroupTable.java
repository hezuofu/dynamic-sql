package io.sketch.dsql.builder;

public interface GroupTable<T> {
    Field<T, Long> star();
}
