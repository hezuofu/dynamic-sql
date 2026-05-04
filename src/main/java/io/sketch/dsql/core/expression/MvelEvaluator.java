package io.sketch.dsql.core.expression;

import io.sketch.dsql.exception.ExpressionException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MvelEvaluator implements ExpressionEvaluator {

    private final ParserContext parserContext = new ParserContext();
    private final Map<String, Object> compiledExpressions = new ConcurrentHashMap<>();

    public MvelEvaluator() {
        parserContext.setStrictTypeEnforcement(false);
    }

    public void registerFunction(String name, Object function) {
        if (function instanceof Class<?>) {
            parserContext.addImport(name, (Class<?>) function);
        } else if (function instanceof Method) {
            parserContext.addImport(name, (Method) function);
        }
    }

    public void registerFunctions(Map<String, Object> functions) {
        functions.forEach(this::registerFunction);
    }

    @Override
    public Object evaluate(String expression, Map<String, Object> variables) {
        try {
            Map<String, Object> context = new HashMap<>(variables);
            Object compiled = compiledExpressions.computeIfAbsent(expression, 
                key -> MVEL.compileExpression(key, parserContext));
            return MVEL.executeExpression(compiled, context);
        } catch (Exception e) {
            throw new ExpressionException("Failed to evaluate expression", expression, e);
        }
    }
}