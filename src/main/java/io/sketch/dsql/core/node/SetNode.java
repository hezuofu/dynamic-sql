package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.Objects;

public class SetNode implements SqlNode {

    private final SqlNode body;

    public SetNode(SqlNode body) {
        this.body = body;
    }

    @Override
    public void apply(DynamicContext context) {
        int start = context.getSqlLength();
        body.apply(context);
        String content = context.getSql().substring(start);
        
        content = content.trim();
        content = trimSuffix(content, ",");
        
        if (!content.isEmpty()) {
            context.setSqlLength(start);
            context.appendSql("SET ");
            context.appendSql(content);
        } else {
            context.setSqlLength(start);
        }
    }

    private String trimSuffix(String str, String... suffixes) {
        String result = str;
        for (String suffix : suffixes) {
            while (result.endsWith(suffix)) {
                result = result.substring(0, result.length() - suffix.length()).trim();
            }
        }
        return result;
    }

    public SqlNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SetNode setNode = (SetNode) o;
        return Objects.equals(body, setNode.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "SetNode{" +
                "body=" + body +
                '}';
    }
}