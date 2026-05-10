package io.sketch.dsql.builder;

import java.util.List;

public interface Predicate {

    record ColumnPredicate(Field<?,?> field, Op op, List<Object> values) implements Predicate {}
    record Junction(Predicate left, Logic op, Predicate right) implements Predicate {}
    record Negation(Predicate operand) implements Predicate {}
    record Raw(String sql, List<Object> params) implements Predicate {}

    enum Op {
        EQ("="), NEQ("<>"), GT(">"), GTE(">="), LT("<"), LTE("<="),
        LIKE("LIKE"), IN("IN"), NOT_IN("NOT IN"), BETWEEN("BETWEEN"),
        IS_NULL("IS NULL"), IS_NOT_NULL("IS NOT NULL");
        private final String sql;
        Op(String s) { sql = s; }
        public String sql() { return sql; }
    }

    enum Logic { AND, OR }

    static void render(Predicate p, StringBuilder sql, List<Object> params) {
        if (p instanceof ColumnPredicate cp) {
            sql.append(cp.field().name()).append(' ').append(cp.op().sql());
            switch (cp.op()) {
                case IS_NULL: case IS_NOT_NULL: break;
                case IN: case NOT_IN: {
                    sql.append(" (");
                    for (int i = 0; i < cp.values().size(); i++) {
                        if (i > 0) sql.append(", "); sql.append('?');
                    }
                    sql.append(')'); params.addAll(cp.values()); break;
                }
                case BETWEEN: sql.append(" ? AND ?"); params.addAll(cp.values()); break;
                default: sql.append(" ?"); params.addAll(cp.values()); break;
            }
        } else if (p instanceof Junction j) {
            sql.append('('); render(j.left(), sql, params);
            sql.append(") ").append(j.op().name()).append(" (");
            render(j.right(), sql, params); sql.append(')');
        } else if (p instanceof Negation n) {
            sql.append("NOT ("); render(n.operand(), sql, params); sql.append(')');
        } else if (p instanceof Raw r) {
            sql.append(r.sql()); params.addAll(r.params());
        }
    }
}
