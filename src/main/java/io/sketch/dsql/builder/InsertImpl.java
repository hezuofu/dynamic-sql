package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlParameter;
import io.sketch.dsql.core.result.SqlResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

final class InsertImpl<T> implements Insert<T> {

    private final String table;
    private final List<Field<T,?>> cols = new ArrayList<>();
    private final List<List<Object>> rows = new ArrayList<>();
    private Select<?> subquery;

    InsertImpl(Class<T> type) { this.table = EntityResolver.tableName(type); }

    @SafeVarargs
    public final Insert<T> columns(Field<T,?>... cs) { cols.addAll(Arrays.asList(cs)); return this; }

    public Insert<T> values(Object... vs) { rows.add(Arrays.asList(vs)); return this; }
    public Insert<T> row(Object... vs) { return values(vs); }
    public Insert<T> select(Select<?> sq) { this.subquery = sq; return this; }

    public SqlResult build() {
        StringBuilder sql = new StringBuilder(); List<Object> params = new ArrayList<>();
        sql.append("INSERT INTO ").append(table);
        if (!cols.isEmpty()) sql.append(" (").append(cols.stream().map(Field::name).collect(Collectors.joining(", "))).append(')');
        if (subquery != null) {
            sql.append(' ').append(subquery.build().getSql());
        } else {
            sql.append(" VALUES ");
            for (int i = 0; i < rows.size(); i++) {
                if (i > 0) sql.append(", ");
                sql.append('(');
                List<Object> row = rows.get(i);
                for (int j = 0; j < row.size(); j++) {
                    if (j > 0) sql.append(", "); sql.append('?'); params.add(row.get(j));
                }
                sql.append(')');
            }
        }
        AtomicInteger c = new AtomicInteger(0);
        return SqlResult.builder().sql(sql.toString()).parameters(
            params.stream().map(v -> SqlParameter.builder().name("p" + c.incrementAndGet()).value(v).build()).collect(Collectors.toList())
        ).build();
    }
}
