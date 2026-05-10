package io.sketch.dsql.core.expression;

import java.util.Map;

@FunctionalInterface
public interface ExpressionEvaluator {

    Object evaluate(String expression, Map<String, Object> variables);

    default boolean evaluateBoolean(String expression, Map<String, Object> variables) {
        Object result = evaluate(expression, variables);
        if (result == null) return false;
        if (result instanceof Boolean) return (Boolean) result;
        if (result instanceof Number) return ((Number) result).doubleValue() != 0;
        if (result instanceof CharSequence) return !((CharSequence) result).isEmpty();
        return true;
    }

    default String evaluateString(String expression, Map<String, Object> variables) {
        Object result = evaluate(expression, variables);
        return result != null ? result.toString() : null;
    }

    default Number evaluateNumber(String expression, Map<String, Object> variables) {
        Object result = evaluate(expression, variables);
        return result instanceof Number ? (Number) result : null;
    }
}