package io.sketch.dsql.builder;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Query<T> extends Statement {

    <J> Query<T> innerJoin(Class<J> entity, BiFunction<T, J, Predicate> on);
    <J> Query<T> leftJoin(Class<J> entity, BiFunction<T, J, Predicate> on);

    Filtered<T> where(Consumer<T> callback);
    Grouped<T> groupBy(Function<T, GroupKeys> fn);
    Projected<T> select(Function<GroupRef<T>, List<Object>> fn);
}
