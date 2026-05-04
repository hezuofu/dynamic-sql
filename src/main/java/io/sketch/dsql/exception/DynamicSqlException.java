package io.sketch.dsql.exception;

public class DynamicSqlException extends RuntimeException {

    public DynamicSqlException(String message) {
        super(message);
    }

    public DynamicSqlException(String message, Throwable cause) {
        super(message, cause);
    }

    public DynamicSqlException(Throwable cause) {
        super(cause);
    }
}