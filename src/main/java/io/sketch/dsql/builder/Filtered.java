package io.sketch.dsql.builder;

import java.util.List;
import java.util.function.Function;

public interface Filtered<T> extends Statement {
    Grouped<T> groupBy(Function<T, GroupKeys> fn);
    Projected<T> select(Function<GroupRef<T>, List<Object>> fn);
}
