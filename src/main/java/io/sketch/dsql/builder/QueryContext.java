package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlParameter;
import io.sketch.dsql.core.result.SqlResult;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;

final class QueryContext<T> {

    // State
    final Class<T> type;
    final T proxy;
    final String table;
    final List<Join> joins = new ArrayList<>();
    Predicate where;
    final List<String> groupBy = new ArrayList<>();
    Predicate having;
    final List<Col> selectCols = new ArrayList<>();
    final List<Order> orderBy = new ArrayList<>();
    int limit, offset;
    final List<QueryContext<?>> unions = new ArrayList<>();
    boolean unionAll;

    QueryContext(Class<T> type) {
        this.type = type;
        this.proxy = EntityResolver.create(type);
        this.table = EntityResolver.tableName(type);
    }

    // ── Stage implementations (each implements one interface) ──

    class QImpl implements Query<T> {
        public <J> Query<T> innerJoin(Class<J> e, BiFunction<T, J, Predicate> on) { return join("INNER JOIN", e, on); }
        public <J> Query<T> leftJoin(Class<J> e, BiFunction<T, J, Predicate> on) { return join("LEFT JOIN", e, on); }

        @SuppressWarnings("unchecked")
        private <J> Query<T> join(String type, Class<J> e, BiFunction<T, J, Predicate> on) {
            J jp = EntityResolver.create(e);
            String a = "t" + (joins.size() + 2);
            joins.add(new Join(type, EntityResolver.tableName(e), a, on.apply(proxy, jp)));
            return this;
        }

        @SuppressWarnings("unchecked")
        public Filtered<T> where(Consumer<T> cb) {
            PredicateCollector c = new PredicateCollector();
            T t = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                    (p, m, a) -> { Field<?,?> f = EntityResolver.field(type, m); return f != null ? new FieldTracker<>(f, c) : null; });
            cb.accept(t); where = c.build(); return new FImpl();
        }

        public Grouped<T> groupBy(Function<T, GroupKeys> fn) {
            groupBy.addAll(fn.apply(proxy).columns()); return new GImpl();
        }

        public Projected<T> select(Function<GroupRef<T>, List<Object>> fn) {
            List<Object> items = fn.apply(new GRImpl());
            for (Object item : items) {
                if (item instanceof Field<?,?> f) selectCols.add(new Col("t1." + f.name(), f.name(), f.name()));
                else if (item instanceof Aggregate a) selectCols.add(Col.fromAgg(a));
            } return new PImpl();
        }
        public SqlResult build() { return QueryContext.this.build(); }
        QueryContext<T> ctx() { return QueryContext.this; }
    }

    class FImpl implements Filtered<T> {
        public Grouped<T> groupBy(Function<T, GroupKeys> fn) { return new QImpl().groupBy(fn); }
        public Projected<T> select(Function<GroupRef<T>, List<Object>> fn) { return new QImpl().select(fn); }
        public SqlResult build() { return QueryContext.this.build(); }
    }

    class GImpl implements Grouped<T> {
        public Filtered<T> having(Consumer<GroupRef<T>> cb) {
            PredicateCollector c = new PredicateCollector();
            HGroupRef gr = new HGroupRef(c);
            cb.accept(gr); having = c.build(); return new FImpl();
        }
        public Projected<T> select(Function<GroupRef<T>, List<Object>> fn) { return new QImpl().select(fn); }
        public SqlResult build() { return QueryContext.this.build(); }
    }

    class PImpl implements Projected<T> {
        public Ordered<T> orderBy(Consumer<SelectRef<T>> cb) {
            OTRef sr = new OTRef(); cb.accept(sr); orderBy.addAll(sr.specs); return new OImpl();
        }
        public Limited<T> limit(int n) { limit = n; return new LImpl(); }
        public Limited<T> limit(int o, int n) { offset = o; limit = n; return new LImpl(); }
        public SqlResult build() { return QueryContext.this.build(); }
    }

    class OImpl implements Ordered<T> {
        public Limited<T> limit(int n) { return new PImpl().limit(n); }
        public Limited<T> limit(int o, int n) { return new PImpl().limit(o, n); }
        public SqlResult build() { return QueryContext.this.build(); }
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    class LImpl implements Limited<T> {
        @SafeVarargs public final Limited<T> union(Query<?>... qs) {
            for (Query<?> q : qs) {
                if (q instanceof QueryContext.QImpl qi) unions.add(qi.ctx());
            }
            return this;
        }
        public SqlResult toList() { return QueryContext.this.build(); }
        public SqlResult build() { return toList(); }
    }

    QueryContext<T> ctx() { return QueryContext.this; }

    // ── GroupRef / SelectRef implementations ──

    class GRImpl implements GroupRef<T> {
        @SuppressWarnings("unchecked")
        public GroupTable<T> group() { return (GroupTable<T>) proxy; }
        public Field<T, ?> key1() { return key(0); }
        public Field<T, ?> key2() { return key(1); }
        public Field<T, ?> key3() { return key(2); }
        private Field<T, ?> key(int i) { return Field.of(groupBy.get(i), Object.class); }
    }

    class HGroupRef implements GroupRef<T> {
        private final PredicateCollector c;
        HGroupRef(PredicateCollector c) { this.c = c; }
        @SuppressWarnings("unchecked")
        public GroupTable<T> group() {
            return (GroupTable<T>) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type, GroupTable.class},
                    (p, m, a) -> {
                        if (m.getDeclaringClass() == GroupTable.class && "star".equals(m.getName()))
                            return new FieldTracker<>(Field.of("*", Long.class), c);
                        Field<?,?> f = EntityResolver.field(type, m);
                        return f != null ? new FieldTracker<>(f, c) : null;
                    });
        }
        public Field<T, ?> key1() { return key(0); }
        public Field<T, ?> key2() { return key(1); }
        public Field<T, ?> key3() { return key(2); }
        private Field<T, ?> key(int i) { return Field.of(groupBy.get(i), Object.class); }
    }

    class OTRef implements SelectRef<T> {
        final List<Order> specs = new ArrayList<>();
        public Field<T, ?> col(String alias) { return new OTField(Field.of(alias, Object.class)); }
        class OTField extends Field<T, Object> {
            OTField(Field<T, Object> d) { super(d.name(), d.type()); }
            @Override public Order asc()  { Order o = super.asc(); specs.add(o); return o; }
            @Override public Order desc() { Order o = super.desc(); specs.add(o); return o; }
        }
    }

    // ── SQL Rendering ──

    SqlResult build() {
        StringBuilder sql = new StringBuilder(); List<Object> params = new ArrayList<>();
        if (!unions.isEmpty()) renderUnion(sql, params);
        else if (!selectCols.isEmpty()) renderWrapped(sql, params);
        else renderFlat(sql, params);
        AtomicInteger c = new AtomicInteger(0);
        List<SqlParameter> sp = params.stream().map(v -> SqlParameter.builder().name("p" + c.incrementAndGet()).value(v).build()).collect(Collectors.toList());
        return SqlResult.builder().sql(sql.toString()).parameters(sp).build();
    }

    void renderFlat(StringBuilder s, List<Object> p) {
        s.append("SELECT * FROM ").append(table);
        join(s, p); where(s, p); groupBy(s); having(s, p);
    }

    void renderWrapped(StringBuilder s, List<Object> p) {
        s.append("SELECT ");
        for (int i = 0; i < selectCols.size(); i++) {
            if (i > 0) s.append(", ");
            Col c = selectCols.get(i);
            s.append(c.outer);
            if (c.alias != null) s.append(" AS ").append(c.alias);
        }
        Set<String> inner = new LinkedHashSet<>(groupBy);
        for (Col c : selectCols) inner.addAll(c.innerCols);
        s.append(" FROM (SELECT ").append(String.join(", ", inner)).append(" FROM ").append(table);
        join(s, p); where(s, p); groupBy(s); having(s, p);
        s.append(") t1");
        if (!orderBy.isEmpty()) {
            s.append(" ORDER BY ");
            for (int i = 0; i < orderBy.size(); i++) {
                if (i > 0) s.append(", ");
                s.append("t1.").append(orderBy.get(i).column()).append(' ').append(orderBy.get(i).direction());
            }
        }
        limit(s, p);
    }

    void renderUnion(StringBuilder s, List<Object> p) {
        String sep = unionAll ? " UNION ALL " : " UNION ";
        renderSingle(s, p);
        for (QueryContext<?> u : unions) { s.append(sep); u.renderSingle(s, p); }
        limit(s, p);
    }

    void renderSingle(StringBuilder s, List<Object> p) {
        if (!selectCols.isEmpty()) renderWrapped(s, p); else renderFlat(s, p);
    }

    void join(StringBuilder s, List<Object> p) {
        for (Join j : joins) {
            s.append(' ').append(j.type).append(' ').append(j.table).append(' ').append(j.alias);
            if (j.on != null) { s.append(" ON "); Predicate.render(j.on, s, p); }
        }
    }

    void where(StringBuilder s, List<Object> p) { if (where != null) { s.append(" WHERE "); Predicate.render(where, s, p); } }
    void groupBy(StringBuilder s) { if (!groupBy.isEmpty()) s.append(" GROUP BY ").append(String.join(", ", groupBy)); }
    void having(StringBuilder s, List<Object> p) { if (having != null) { s.append(" HAVING "); Predicate.render(having, s, p); } }
    void limit(StringBuilder s, List<Object> p) {
        if (limit > 0) {
            if (offset > 0) { s.append(" LIMIT ?, ?"); p.add(offset); p.add(limit); }
            else { s.append(" LIMIT ?"); p.add(limit); }
        }
    }

    record Join(String type, String table, String alias, Predicate on) {}
    record Col(String outer, String alias, Set<String> innerCols) {
        Col(String outer, String alias, String... cols) { this(outer, alias, new LinkedHashSet<>(Arrays.asList(cols))); }
        static Col fromAgg(Aggregate a) {
            String o = a.renderForOuter();
            String c = a.column();
            return new Col(o, a.alias(), c != null && !"*".equals(c) ? Set.of(c) : Set.of());
        }
    }
}
