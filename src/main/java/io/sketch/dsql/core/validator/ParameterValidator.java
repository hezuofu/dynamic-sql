package io.sketch.dsql.core.validator;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterValidator implements SqlValidator {

    private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\$\\{([^}]+)}|#\\{([^}]+)}|@([a-zA-Z_][a-zA-Z0-9_]*)");

    @Override
    public ValidationResult validate(String script) {
        ValidationResult result = new ValidationResult();

        if (script == null || script.trim().isEmpty()) {
            return result;
        }

        Matcher matcher = PARAMETER_PATTERN.matcher(script);
        while (matcher.find()) {
            String paramName = matcher.group(1);
            if (paramName == null) paramName = matcher.group(2);
            if (paramName == null) paramName = matcher.group(3);
            
            validateParameterName(paramName, result);
        }

        return result;
    }

    private void validateParameterName(String name, ValidationResult result) {
        if (name == null || name.isEmpty()) {
            result.addError("parameter", "Empty parameter name", "PARAM_EMPTY");
            return;
        }

        if (!name.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            result.addError("parameter", "Invalid parameter name: " + name, "PARAM_INVALID");
        }
    }

    public ValidationResult validateParameters(Map<String, Object> parameters, List<String> requiredParams) {
        ValidationResult result = new ValidationResult();

        if (requiredParams == null || requiredParams.isEmpty()) {
            return result;
        }

        for (String param : requiredParams) {
            if (!parameters.containsKey(param)) {
                result.addError("parameter", "Missing required parameter: " + param, "PARAM_MISSING");
            } else if (parameters.get(param) == null) {
                result.addError("parameter", "Parameter value is null: " + param, "PARAM_NULL");
            }
        }

        return result;
    }
}