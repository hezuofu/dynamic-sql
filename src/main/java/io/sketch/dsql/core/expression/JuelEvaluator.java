package io.sketch.dsql.core.expression;

import io.sketch.dsql.exception.ExpressionException;
import de.odysseus.el.ExpressionFactoryImpl;
import de.odysseus.el.util.SimpleContext;

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JuelEvaluator implements ExpressionEvaluator {

    private final ExpressionFactory factory = new ExpressionFactoryImpl();
    private final Map<String, ValueExpression> compiledExpressions = new ConcurrentHashMap<>();

    @Override
    public Object evaluate(String expression, Map<String, Object> variables) {
        try {
            final String juelExpr;
            if (expression.startsWith("${") && expression.endsWith("}")) {
                juelExpr = expression;
            } else {
                juelExpr = "${" + expression + "}";
            }
            
            ValueExpression exp = compiledExpressions.computeIfAbsent(expression, 
                key -> factory.createValueExpression(new SimpleContext(), juelExpr, Object.class));
            
            SimpleContext context = new SimpleContext();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                ValueExpression valueExp = factory.createValueExpression(entry.getValue(), Object.class);
                context.setVariable(entry.getKey(), valueExp);
            }
            
            return exp.getValue(context);
        } catch (Exception e) {
            throw new ExpressionException("Failed to evaluate expression", expression, e);
        }
    }

    @Override
    public boolean evaluateBoolean(String expression, Map<String, Object> variables) {
        try {
            final String juelExpr;
            if (expression.startsWith("${") && expression.endsWith("}")) {
                juelExpr = expression;
            } else {
                juelExpr = "${" + expression + "}";
            }
            
            ValueExpression exp = factory.createValueExpression(new SimpleContext(), juelExpr, Boolean.class);
            SimpleContext context = new SimpleContext();
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                ValueExpression valueExp = factory.createValueExpression(entry.getValue(), Object.class);
                context.setVariable(entry.getKey(), valueExp);
            }
            
            Boolean result = (Boolean) exp.getValue(context);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            throw new ExpressionException("Failed to evaluate boolean expression", expression, e);
        }
    }
}