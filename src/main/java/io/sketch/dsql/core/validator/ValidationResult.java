package io.sketch.dsql.core.validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ValidationResult {

    private final List<Error> errors = new ArrayList<>();

    public void addError(String field, String message) {
        errors.add(new Error(field, message));
    }

    public void addError(String field, String message, String code) {
        errors.add(new Error(field, message, code));
    }

    public List<Error> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public int getErrorCount() {
        return errors.size();
    }

    public void merge(ValidationResult other) {
        if (other != null) {
            this.errors.addAll(other.errors);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return Objects.equals(errors, that.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "errors=" + errors +
                '}';
    }

    public static class Error {
        private final String field;
        private final String message;
        private final String code;

        public Error(String field, String message) {
            this.field = field;
            this.message = message;
            this.code = null;
        }

        public Error(String field, String message, String code) {
            this.field = field;
            this.message = message;
            this.code = code;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Error error = (Error) o;
            return Objects.equals(field, error.field) &&
                    Objects.equals(message, error.message) &&
                    Objects.equals(code, error.code);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field, message, code);
        }

        @Override
        public String toString() {
            return "Error{" +
                    "field='" + field + '\'' +
                    ", message='" + message + '\'' +
                    ", code='" + code + '\'' +
                    '}';
        }
    }
}