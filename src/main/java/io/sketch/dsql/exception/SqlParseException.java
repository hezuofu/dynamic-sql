package io.sketch.dsql.exception;

public class SqlParseException extends DynamicSqlException {

    private final int lineNumber;
    private final int columnNumber;

    public SqlParseException(String message) {
        super(message);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public SqlParseException(String message, Throwable cause) {
        super(message, cause);
        this.lineNumber = -1;
        this.columnNumber = -1;
    }

    public SqlParseException(String message, int lineNumber, int columnNumber) {
        super(message);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public SqlParseException(String message, int lineNumber, int columnNumber, Throwable cause) {
        super(message, cause);
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    @Override
    public String getMessage() {
        if (lineNumber >= 0 && columnNumber >= 0) {
            return String.format("Line %d, Column %d: %s", lineNumber, columnNumber, super.getMessage());
        }
        return super.getMessage();
    }
}