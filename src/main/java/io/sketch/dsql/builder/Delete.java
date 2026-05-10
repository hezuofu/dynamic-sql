package io.sketch.dsql.builder;

import java.util.function.Consumer;

public interface Delete<T> extends Statement {
    FilteredDelete<T> where(Consumer<T> callback);
}