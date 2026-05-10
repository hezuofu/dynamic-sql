package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlParameter;
import io.sketch.dsql.core.result.SqlResult;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

final class UpdateImpl<T> implements Update<T> {

    private final Class<T> type;
    private final String table;
    private final Map<String, Object> sets = new LinkedHashMap<>();
    private Predicate where;

    UpdateImpl(Class<T> type) { this.type = type; this.table = EntityResolver.tableName(type); }

    public Update<T> set(Field<T,?> col, Object val) { sets.put(col.name(), val); return this; }

    @SuppressWarnings("unchecked")
    public Statement where(Consumer<T> cb) {
        PredicateCollector c = new PredicateCollector();
        T t = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (p, m, a) -> { Field<?,?> f = EntityResolver.field(type, m); return f != null ? new FieldTracker<>(f, c) : null; });
        cb.accept(t); where = c.build(); return this::buildImpl;
    }

    public SqlResult build() { return buildImpl(); }

    private SqlResult buildImpl() {
        StringBuilder sql = new StringBuilder(); List<Object> params = new ArrayList<>();
        sql.append("UPDATE ").append(table).append(" SET ");
        int i = 0;
        for (Map.Entry<String, Object> e : sets.entrySet()) {
            if (i > 0) sql.append(", "); sql.append(e.getKey()).append(" = ?"); params.add(e.getValue()); i++;
        }
        if (where != null) { sql.append(" WHERE "); Predicate.render(where, sql, params); }
        AtomicInteger c = new AtomicInteger(0);
        return SqlResult.builder().sql(sql.toString()).parameters(
            params.stream().map(v -> SqlParameter.builder().name("p" + c.incrementAndGet()).value(v).build()).collect(Collectors.toList())
        ).build();
    }
}
