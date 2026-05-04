package io.sketch.dsql.core.expression;

import io.sketch.dsql.exception.ExpressionException;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AviatorExpressionEvaluator implements ExpressionEvaluator {

    private final Map<String, Expression> compiledExpressions = new ConcurrentHashMap<>();

    @Override
    public Object evaluate(String expression, Map<String, Object> variables) {
        try {
            Expression exp = compiledExpressions.computeIfAbsent(expression, AviatorExpressionEvaluator::compile);
            return exp.execute(variables);
        } catch (Exception e) {
            throw new ExpressionException("Failed to evaluate expression", expression, e);
        }
    }

    @Override
    public boolean evaluateBoolean(String expression, Map<String, Object> variables) {
        try {
            Expression exp = compiledExpressions.computeIfAbsent(expression, AviatorExpressionEvaluator::compile);
            Object result = exp.execute(variables);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            throw new ExpressionException("Failed to evaluate boolean expression", expression, e);
        }
    }

    private static Expression compile(String expression) {
        return AviatorEvaluator.compile(expression, true);
    }
}