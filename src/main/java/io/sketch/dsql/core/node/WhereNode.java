package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.List;
import java.util.Objects;

public class WhereNode implements SqlNode {

    private final SqlNode body;

    public WhereNode(SqlNode body) {
        this.body = body;
    }

    private static final List<String> WHERE_PREFIXES = List.of("AND ", "OR ", "AND\t", "OR\t");
    private static final List<String> WHERE_SUFFIXES = List.of("AND", "OR");

    @Override
    public void apply(DynamicContext context) {
        int start = context.getSqlLength();
        body.apply(context);
        String content = context.getSql().substring(start).trim();

        content = SqlTrimUtils.trimPrefixes(content, WHERE_PREFIXES).trim();
        content = SqlTrimUtils.trimSuffixes(content, WHERE_SUFFIXES).trim();

        if (!content.isEmpty()) {
            context.setSqlLength(start);
            context.appendSql("WHERE ");
            context.appendSql(content);
        } else {
            context.setSqlLength(start);
        }
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