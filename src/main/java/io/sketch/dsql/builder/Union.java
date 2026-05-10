package io.sketch.dsql.builder;

import java.util.function.Consumer;

public interface Union extends Statement {
    Union all();
    Union distinct();
    Limited<?> orderBy(Consumer<SelectRef<?>> callback);
    Union limit(int limit);
    Union limit(int offset, int limit);
}
