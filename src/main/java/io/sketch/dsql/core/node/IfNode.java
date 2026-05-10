package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;
import io.sketch.dsql.core.expression.ExpressionEvaluator;

import java.util.Objects;

public class IfNode implements SqlNode {

    private final String test;
    private final SqlNode body;
    private final ExpressionEvaluator evaluator;

    public IfNode(String test, SqlNode body, ExpressionEvaluator evaluator) {
        this.test = test;
        this.body = body;
        this.evaluator = evaluator;
    }

    @Override
    public void apply(DynamicContext context) {
        if (evaluateTest(context)) {
            body.apply(context);
        }
    }

    private boolean evaluateTest(DynamicContext context) {
        Object result = evaluator.evaluate(test, context.getAllVariables());
        if (result == null) return false;
        if (result instanceof Boolean) return (Boolean) result;
        if (result instanceof Number) return ((Number) result).doubleValue() != 0;
        if (result instanceof CharSequence) return !((CharSequence) result).isEmpty();
        return true;
    }

    public String getTest() {
        return test;
    }

    public SqlNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IfNode ifNode = (IfNode) o;
        return Objects.equals(test, ifNode.test) &&
                Objects.equals(body, ifNode.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(test, body);
    }

    @Override
    public String toString() {
        return "IfNode{" +
                "test='" + test + '\'' +
                ", body=" + body +
                '}';
    }
}