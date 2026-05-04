package io.sketch.dsql.core.validator;

@FunctionalInterface
public interface SqlValidator {

    ValidationResult validate(String script);
}