package io.sketch.dsql.builder;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Grouped<T> extends Statement {
    Filtered<T> having(Consumer<GroupRef<T>> callback);
    Projected<T> select(Function<GroupRef<T>, List<Object>> fn);
}
