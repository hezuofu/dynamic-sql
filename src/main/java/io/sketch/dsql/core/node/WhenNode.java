package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;
import io.sketch.dsql.core.expression.ExpressionEvaluator;

import java.util.Objects;

public class WhenNode implements SqlNode {

    private final String test;
    private final SqlNode body;
    private final ExpressionEvaluator evaluator;

    public WhenNode(String test, SqlNode body, ExpressionEvaluator evaluator) {
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

    boolean evaluateTest(DynamicContext context) {
        Object result = evaluator.evaluate(test, context.getAllVariables());
        return Boolean.TRUE.equals(result);
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
        WhenNode whenNode = (WhenNode) o;
        return Objects.equals(test, whenNode.test) &&
                Objects.equals(body, whenNode.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(test, body);
    }

    @Override
    public String toString() {
        return "WhenNode{" +
                "test='" + test + '\'' +
                ", body=" + body +
                '}';
    }
}