package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnionNode implements SqlNode {

    private final List<SqlNode> queries;
    private final boolean all;
    private final boolean distinct;

    public UnionNode(List<SqlNode> queries, boolean all, boolean distinct) {
        this.queries = queries != null ? queries : new ArrayList<>();
        this.all = all;
        this.distinct = distinct;
    }

    @Override
    public void apply(DynamicContext context) {
        if (queries.isEmpty()) {
            return;
        }

        String unionKeyword = all ? "UNION ALL" : "UNION";
        if (distinct && !all) {
            unionKeyword = "UNION DISTINCT";
        }

        boolean first = true;
        for (SqlNode query : queries) {
            if (!first) {
                context.appendSql(" ");
                context.appendSql(unionKeyword);
                context.appendSql(" ");
            }
            query.apply(context);
            first = false;
        }
    }

    public List<SqlNode> getQueries() {
        return new ArrayList<>(queries);
    }

    public boolean isAll() {
        return all;
    }

    public boolean isDistinct() {
        return distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnionNode unionNode = (UnionNode) o;
        return all == unionNode.all &&
                distinct == unionNode.distinct &&
                Objects.equals(queries, unionNode.queries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queries, all, distinct);
    }

    @Override
    public String toString() {
        return "UnionNode{" +
                "queries=" + queries +
                ", all=" + all +
                ", distinct=" + distinct +
                '}';
    }
}