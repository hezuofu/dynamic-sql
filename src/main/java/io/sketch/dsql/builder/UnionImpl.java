package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlParameter;
import io.sketch.dsql.core.result.SqlResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

final class UnionImpl implements Union {

    private final List<QueryContext<?>> queries = new ArrayList<>();
    private boolean all;
    private int limit, offset;

    @SafeVarargs
    UnionImpl(Query<?>... qs) {
        for (Query<?> q : qs) grab(q);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    private void grab(Query<?> q) {
        if (q instanceof QueryContext.QImpl qi) queries.add(qi.ctx());
    }

    public Union all() { all = true; return this; }
    public Union distinct() { all = false; return this; }

    public Limited<?> orderBy(Consumer<SelectRef<?>> cb) {
        return new ULimited();
    }

    public Union limit(int n) { limit = n; return this; }
    public Union limit(int o, int n) { offset = o; limit = n; return this; }

    public SqlResult build() { return new ULimited().build(); }

    private class ULimited implements Limited<Object> {
        @SafeVarargs
        public final Limited<Object> union(Query<?>... qs) {
            for (Query<?> q : qs) grab(q); return this;
        }
        public SqlResult toList() { return build(); }
        public SqlResult build() {
            StringBuilder sql = new StringBuilder(); List<Object> params = new ArrayList<>();
            String sep = all ? " UNION ALL " : " UNION ";
            for (int i = 0; i < queries.size(); i++) {
                if (i > 0) sql.append(sep);
                queries.get(i).renderSingle(sql, params);
            }
            if (limit > 0) {
                if (offset > 0) { sql.append(" LIMIT ?, ?"); params.add(offset); params.add(limit); }
                else { sql.append(" LIMIT ?"); params.add(limit); }
            }
            AtomicInteger c = new AtomicInteger(0);
            return SqlResult.builder().sql(sql.toString()).parameters(
                params.stream().map(v -> SqlParameter.builder().name("p" + c.incrementAndGet()).value(v).build()).collect(Collectors.toList())
            ).build();
        }
    }
}
