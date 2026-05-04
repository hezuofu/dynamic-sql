package io.sketch.dsql.exception;

public class SqlValidateException extends DynamicSqlException {

    private final String field;
    private final String code;

    public SqlValidateException(String message) {
        super(message);
        this.field = null;
        this.code = null;
    }

    public SqlValidateException(String message, Throwable cause) {
        super(message, cause);
        this.field = null;
        this.code = null;
    }

    public SqlValidateException(String message, String field, String code) {
        super(message);
        this.field = field;
        this.code = code;
    }

    public SqlValidateException(String message, String field, String code, Throwable cause) {
        super(message, cause);
        this.field = field;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        if (field != null) {
            sb.append(" [field: ").append(field).append("]");
        }
        if (code != null) {
            sb.append(" [code: ").append(code).append("]");
        }
        return sb.toString();
    }
}