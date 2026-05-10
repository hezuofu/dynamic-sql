package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlResult;

public interface Limited<T> extends Statement {
    @SuppressWarnings("unchecked")
    Limited<T> union(Query<?>... others);
    SqlResult toList();
}
