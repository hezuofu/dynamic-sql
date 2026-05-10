package io.sketch.dsql.builder;

import java.util.function.Consumer;

public interface Delete<T> extends Statement {
    Statement where(Consumer<T> callback);
}