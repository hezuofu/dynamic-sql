package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.Objects;

public class OtherwiseNode implements SqlNode {

    private final SqlNode body;

    public OtherwiseNode(SqlNode body) {
        this.body = body;
    }

    @Override
    public void apply(DynamicContext context) {
        body.apply(context);
    }

    public SqlNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OtherwiseNode that = (OtherwiseNode) o;
        return Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "OtherwiseNode{" +
                "body=" + body +
                '}';
    }
}