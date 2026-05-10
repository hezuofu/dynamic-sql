package io.sketch.dsql.builder;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Select<T> extends Statement {

    // SELECT cols FROM ...
    Filtered<T> select(Function<T, List<Field<T,?>>> fn);

    // WHERE ... (implicit SELECT *)
    Filtered<T> where(Consumer<T> callback);

    // GROUP BY ...
    Grouped<T> groupBy(Function<T, GroupKeys> fn);

    // JOIN
    <J> Select<T> innerJoin(Class<J> entity, BiFunction<T, J, Predicate> on);
    <J> Select<T> leftJoin(Class<J> entity, BiFunction<T, J, Predicate> on);
}
