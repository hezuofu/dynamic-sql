package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WithNode implements SqlNode {

    private final String alias;
    private final SqlNode subquery;
    private final boolean recursive;
    private final SqlNode body;

    public WithNode(String alias, SqlNode subquery, boolean recursive, SqlNode body) {
        this.alias = alias;
        this.subquery = subquery;
        this.recursive = recursive;
        this.body = body;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql("WITH ");
        if (recursive) {
            context.appendSql("RECURSIVE ");
        }
        context.appendSql(alias);
        context.appendSql(" AS (");
        subquery.apply(context);
        context.appendSql(") ");
        body.apply(context);
    }

    public String getAlias() {
        return alias;
    }

    public SqlNode getSubquery() {
        return subquery;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public SqlNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WithNode withNode = (WithNode) o;
        return recursive == withNode.recursive &&
                Objects.equals(alias, withNode.alias) &&
                Objects.equals(subquery, withNode.subquery) &&
                Objects.equals(body, withNode.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alias, subquery, recursive, body);
    }

    @Override
    public String toString() {
        return "WithNode{" +
                "alias='" + alias + '\'' +
                ", recursive=" + recursive +
                ", subquery=" + subquery +
                ", body=" + body +
                '}';
    }
}