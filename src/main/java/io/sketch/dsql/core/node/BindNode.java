package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;
import io.sketch.dsql.core.expression.ExpressionEvaluator;

import java.util.Objects;

public class BindNode implements SqlNode {

    private final String name;
    private final String expression;
    private final ExpressionEvaluator evaluator;

    public BindNode(String name, String expression, ExpressionEvaluator evaluator) {
        this.name = name;
        this.expression = expression;
        this.evaluator = evaluator;
    }

    @Override
    public void apply(DynamicContext context) {
        Object value = evaluator.evaluate(expression, context.getAllVariables());
        context.setVariable(name, value);
    }

    public String getName() {
        return name;
    }

    public String getExpression() {
        return expression;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BindNode bindNode = (BindNode) o;
        return Objects.equals(name, bindNode.name) &&
                Objects.equals(expression, bindNode.expression);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, expression);
    }

    @Override
    public String toString() {
        return "BindNode{" +
                "name='" + name + '\'' +
                ", expression='" + expression + '\'' +
                '}';
    }
}