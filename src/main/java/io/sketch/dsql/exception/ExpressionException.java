package io.sketch.dsql.exception;

public class ExpressionException extends DynamicSqlException {

    private final String expression;

    public ExpressionException(String message) {
        super(message);
        this.expression = null;
    }

    public ExpressionException(String message, Throwable cause) {
        super(message, cause);
        this.expression = null;
    }

    public ExpressionException(String message, String expression) {
        super(message);
        this.expression = expression;
    }

    public ExpressionException(String message, String expression, Throwable cause) {
        super(message, cause);
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public String getMessage() {
        if (expression != null) {
            return String.format("Expression '%s': %s", expression, super.getMessage());
        }
        return super.getMessage();
    }
}