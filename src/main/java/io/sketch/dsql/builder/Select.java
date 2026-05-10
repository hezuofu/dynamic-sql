package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlResult;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Select<T> extends Statement {

    // WHERE
    Select<T> where(Consumer<T> callback);

    // GROUP BY
    Select<T> groupBy(Function<T, GroupKeys> fn);

    // HAVING
    Select<T> having(Consumer<GroupRef<T>> callback);

    // Subquery-wrapping SELECT (after GROUP BY/HAVING)
    Projected<T> select(Function<GroupRef<T>, List<Object>> fn);

    // ORDER BY
    Select<T> orderBy(Consumer<SelectRef<T>> callback);

    // LIMIT / OFFSET
    Select<T> limit(int limit);
    Select<T> limit(int offset, int limit);

    // UNION
    @SuppressWarnings("unchecked")
    Select<T> union(Select<T>... others);

    @SuppressWarnings("unchecked")
    Select<T> unionAll(Select<T>... others);

    // JOIN
    <J> Select<T> innerJoin(Class<J> entity, BiFunction<T, J, Predicate> on);
    <J> Select<T> leftJoin(Class<J> entity, BiFunction<T, J, Predicate> on);
    <J> Select<T> rightJoin(Class<J> entity, BiFunction<T, J, Predicate> on);

    // WINDOW
    Select<T> window(Window... windows);

    SqlResult toList();
}
