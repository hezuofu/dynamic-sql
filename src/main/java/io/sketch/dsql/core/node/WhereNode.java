package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.Objects;

public class WhereNode implements SqlNode {

    private final SqlNode body;

    public WhereNode(SqlNode body) {
        this.body = body;
    }

    @Override
    public void apply(DynamicContext context) {
        int start = context.getSqlLength();
        body.apply(context);
        String content = context.getSql().substring(start);
        
        content = content.trim();
        content = trimPrefix(content, "AND ", "OR ");
        content = trimSuffix(content, "AND", "OR");
        
        if (!content.isEmpty()) {
            context.setSqlLength(start);
            context.appendSql("WHERE ");
            context.appendSql(content);
        } else {
            context.setSqlLength(start);
        }
    }

    private String trimPrefix(String str, String... prefixes) {
        String result = str;
        for (String prefix : prefixes) {
            while (result.startsWith(prefix)) {
                result = result.substring(prefix.length()).trim();
            }
        }
        return result;
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
        WhereNode whereNode = (WhereNode) o;
        return Objects.equals(body, whereNode.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body);
    }

    @Override
    public String toString() {
        return "WhereNode{" +
                "body=" + body +
                '}';
    }
}