package io.sketch.dsql.builder;

import io.sketch.dsql.exception.DynamicSqlException;

public class SqlBuilderException extends DynamicSqlException {
    public SqlBuilderException(String msg) { super(msg); }
    public SqlBuilderException(String msg, Throwable cause) { super(msg, cause); }
}
