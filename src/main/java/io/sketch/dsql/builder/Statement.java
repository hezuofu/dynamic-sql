package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlResult;

public interface Statement {
    SqlResult build();
}
