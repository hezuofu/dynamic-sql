package io.sketch.dsql.builder;

import java.util.function.Consumer;

public interface Update<T> extends Statement {
    Update<T> set(Field<T,?> col, Object val);
    FilteredUpdate<T> where(Consumer<T> callback);
}