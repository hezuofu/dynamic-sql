package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.Objects;

public class JoinNode implements SqlNode {

    public enum JoinType {
        INNER, LEFT, RIGHT, FULL, CROSS, NATURAL
    }

    private final JoinType joinType;
    private final String table;
    private final String alias;
    private final String onCondition;

    public JoinNode(JoinType joinType, String table, String alias, String onCondition) {
        this.joinType = joinType;
        this.table = table;
        this.alias = alias;
        this.onCondition = onCondition;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(joinType.name());
        context.appendSql(" JOIN ");
        context.appendSql(table);
        if (alias != null && !alias.isEmpty()) {
            context.appendSql(" AS ");
            context.appendSql(alias);
        }
        if (onCondition != null && !onCondition.isEmpty()) {
            context.appendSql(" ON ");
            context.appendSql(onCondition);
        }
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public String getTable() {
        return table;
    }

    public String getAlias() {
        return alias;
    }

    public String getOnCondition() {
        return onCondition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinNode joinNode = (JoinNode) o;
        return joinType == joinNode.joinType &&
                Objects.equals(table, joinNode.table) &&
                Objects.equals(alias, joinNode.alias) &&
                Objects.equals(onCondition, joinNode.onCondition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(joinType, table, alias, onCondition);
    }

    @Override
    public String toString() {
        return "JoinNode{" +
                "joinType=" + joinType +
                ", table='" + table + '\'' +
                ", alias='" + alias + '\'' +
                ", onCondition='" + onCondition + '\'' +
                '}';
    }
}