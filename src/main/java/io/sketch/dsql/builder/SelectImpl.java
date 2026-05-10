package io.sketch.dsql.builder;

import io.sketch.dsql.core.result.SqlParameter;
import io.sketch.dsql.core.result.SqlResult;

import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;
import java.util.stream.Collectors;

final class SelectImpl<T> implements Select<T> {

    final Class<T> type;
    final T proxy;
    final String table;
    final List<String> cols = new ArrayList<>();           // SELECT columns
    final List<Join> joins = new ArrayList<>();
    Predicate where;
    final List<String> groupBy = new ArrayList<>();
    Predicate having;
    final List<SelectImpl<?>> unions = new ArrayList<>();
    boolean unionAll;
    final List<OrderCol> projectedCols = new ArrayList<>(); // subquery-wrapped
    final List<Order> orderBy = new ArrayList<>();
    int limit, offset;
    final List<Window> windows = new ArrayList<>();
    final List<Cte> ctes = new ArrayList<>();

    SelectImpl(Class<T> type) { this(type, EntityResolver.tableName(type), List.of()); }
    SelectImpl(Class<T> type, String table, List<Cte> ctes) {
        this.type = type; this.table = table; this.ctes.addAll(ctes);
        this.proxy = EntityResolver.create(type);
    }

    // -- WHERE --
    @SuppressWarnings("unchecked")
    public Select<T> where(Consumer<T> cb) {
        PredicateCollector c = new PredicateCollector();
        T t = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (p, m, a) -> { Field<?,?> f = EntityResolver.field(type, m); return f != null ? new FieldTracker<>(f, c) : null; });
        cb.accept(t);
        Predicate extra = c.build();
        if (extra != null) where = where == null ? extra : new Predicate.Junction(where, Predicate.Logic.AND, extra);
        return this;
    }

    // -- GROUP BY --
    public Select<T> groupBy(Function<T, GroupKeys> fn) { groupBy.addAll(fn.apply(proxy).columns()); return this; }

    // -- HAVING --
    public Select<T> having(Consumer<GroupRef<T>> cb) {
        PredicateCollector c = new PredicateCollector();
        cb.accept(new HGroupRef(c)); having = c.build(); return this;
    }

    // -- Subquery-wrapping SELECT --
    public Projected<T> select(Function<GroupRef<T>, List<Object>> fn) {
        List<Object> items = fn.apply(new GRImpl());
        for (Object item : items) {
            if (item instanceof Field<?,?> f) projectedCols.add(new OrderCol("t1." + f.name(), f.name(), f.name()));
            else if (item instanceof Aggregate a) projectedCols.add(OrderCol.fromAgg(a));
        }
        return new PImpl();
    }

    // -- ORDER BY --
    public Select<T> orderBy(Consumer<SelectRef<T>> cb) { OTRef sr = new OTRef(orderBy); cb.accept(sr); return this; }

    // -- LIMIT --
    public Select<T> limit(int n) { limit = n; return this; }
    public Select<T> limit(int o, int n) { offset = o; limit = n; return this; }

    // -- UNION --
    @SuppressWarnings({"unchecked","rawtypes"})
    public Select<T> union(Select<T>... qs) { unionAll = false; for (Select<T> q : qs) unions.add((SelectImpl<T>) q); return this; }
    @SuppressWarnings({"unchecked","rawtypes"})
    public Select<T> unionAll(Select<T>... qs) { unionAll = true; for (Select<T> q : qs) unions.add((SelectImpl<T>) q); return this; }

    // -- JOIN --
    @SuppressWarnings("unchecked")
    public <J> Select<T> innerJoin(Class<J> e, BiFunction<T, J, Predicate> on) { return join("INNER JOIN", e, on); }
    public <J> Select<T> leftJoin(Class<J> e, BiFunction<T, J, Predicate> on) { return join("LEFT JOIN", e, on); }
    public <J> Select<T> rightJoin(Class<J> e, BiFunction<T, J, Predicate> on) { return join("RIGHT JOIN", e, on); }
    private <J> Select<T> join(String jt, Class<J> e, BiFunction<T, J, Predicate> on) {
        J jp = EntityResolver.create(e);
        joins.add(new Join(jt, EntityResolver.tableName(e), "t" + (joins.size() + 2), on.apply(proxy, jp)));
        return this;
    }

    // -- WINDOW --
    public Select<T> window(Window... ws) { windows.addAll(Arrays.asList(ws)); return this; }

    // -- Terminal --
    public SqlResult toList() { return build(); }
    public SqlResult build() {
        StringBuilder sql = new StringBuilder(); List<Object> params = new ArrayList<>();
        renderCtes(sql, params);
        if (!unions.isEmpty()) renderUnion(sql, params);
        else if (!projectedCols.isEmpty()) renderWrapped(sql, params);
        else renderFlat(sql, params);
        AtomicInteger c = new AtomicInteger(0);
        List<SqlParameter> sp = params.stream().map(v -> SqlParameter.builder().name("p" + c.incrementAndGet()).value(v).build()).collect(Collectors.toList());
        return SqlResult.builder().sql(sql.toString()).parameters(sp).build();
    }

    // -- Projected implementation --
    class PImpl implements Projected<T> {
        public Projected<T> orderBy(Consumer<SelectRef<T>> cb) { OTRef sr = new OTRef(orderBy); cb.accept(sr); return this; }
        public Projected<T> limit(int n) { limit = n; return this; }
        public Projected<T> limit(int o, int n) { offset = o; limit = n; return this; }
        @SuppressWarnings({"unchecked","rawtypes"})
        public Projected<T> union(Select<T>... qs) { unionAll = false; for (Select<T> q : qs) unions.add((SelectImpl<T>) q); return this; }
        @SuppressWarnings({"unchecked","rawtypes"})
        public Projected<T> unionAll(Select<T>... qs) { unionAll = true; for (Select<T> q : qs) unions.add((SelectImpl<T>) q); return this; }
        public SqlResult toList() { return SelectImpl.this.build(); }
        public SqlResult build() { return toList(); }
    }

    // -- Rendering --
    void renderCtes(StringBuilder s, List<Object> p) {
        if (ctes.isEmpty()) return;
        s.append("WITH ");
        for (int i = 0; i < ctes.size(); i++) {
            if (i > 0) s.append(", ");
            Cte c = ctes.get(i); s.append(c.name);
            if (c.recursive) s.insert(s.length() - c.name.length(), "RECURSIVE ");
            s.append(" AS ("); c.query.buildTo(s, p); s.append(')');
        }
        s.append(' ');
    }

    void buildTo(StringBuilder s, List<Object> p) { // used by CTE
        if (!projectedCols.isEmpty()) renderWrappedTo(s, p); else renderFlatTo(s, p);
    }

    void renderFlat(StringBuilder s, List<Object> p) {
        if (!cols.isEmpty()) s.append("SELECT ").append(String.join(", ", cols)).append(" FROM ").append(table);
        else s.append("SELECT * FROM ").append(table);
        renderJoin(s, p); renderWhere(s, p); renderGroupBy(s); renderHaving(s, p); renderWindow(s); renderOrder(s); renderLimit(s, p);
    }

    void renderWrapped(StringBuilder s, List<Object> p) {
        s.append("SELECT ");
        for (int i = 0; i < projectedCols.size(); i++) {
            if (i > 0) s.append(", "); OrderCol c = projectedCols.get(i);
            s.append(c.outer); if (c.alias != null) s.append(" AS ").append(c.alias);
        }
        Set<String> inner = new LinkedHashSet<>(groupBy);
        for (OrderCol c : projectedCols) inner.addAll(c.innerCols);
        s.append(" FROM (SELECT ").append(String.join(", ", inner)).append(" FROM ").append(table);
        renderJoin(s, p); renderWhere(s, p); renderGroupBy(s); renderHaving(s, p); s.append(") t1");
        renderOrder(s); renderLimit(s, p);
    }

    void renderFlatTo(StringBuilder s, List<Object> p) {
        if (!cols.isEmpty()) s.append("SELECT ").append(String.join(", ", cols)).append(" FROM ").append(table);
        else s.append("SELECT * FROM ").append(table);
        renderJoin(s, p); renderWhere(s, p); renderGroupBy(s); renderHaving(s, p); renderWindow(s); renderOrder(s); renderLimit(s, p);
    }

    void renderWrappedTo(StringBuilder s, List<Object> p) {
        s.append("SELECT ");
        for (int i = 0; i < projectedCols.size(); i++) {
            if (i > 0) s.append(", "); OrderCol c = projectedCols.get(i);
            s.append(c.outer); if (c.alias != null) s.append(" AS ").append(c.alias);
        }
        Set<String> inner = new LinkedHashSet<>(groupBy);
        for (OrderCol c : projectedCols) inner.addAll(c.innerCols);
        s.append(" FROM (SELECT ").append(String.join(", ", inner)).append(" FROM ").append(table);
        renderJoin(s, p); renderWhere(s, p); renderGroupBy(s); renderHaving(s, p); s.append(") t1");
        renderOrder(s); renderLimit(s, p);
    }

    void renderUnion(StringBuilder s, List<Object> p) {
        String sep = unionAll ? " UNION ALL " : " UNION ";
        renderSingle(s, p);
        for (SelectImpl<?> u : unions) { s.append(sep); u.renderSingle(s, p); }
        renderOrder(s); renderLimit(s, p);
    }

    void renderSingle(StringBuilder s, List<Object> p) {
        if (!projectedCols.isEmpty()) renderWrapped(s, p); else renderFlat(s, p);
    }

    void renderJoin(StringBuilder s, List<Object> p) {
        for (Join j : joins) {
            s.append(' ').append(j.type).append(' ').append(j.table).append(' ').append(j.alias);
            if (j.on != null) { s.append(" ON "); Predicate.render(j.on, s, p); }
        }
    }
    void renderWhere(StringBuilder s, List<Object> p) { if (where != null) { s.append(" WHERE "); Predicate.render(where, s, p); } }
    void renderGroupBy(StringBuilder s) { if (!groupBy.isEmpty()) s.append(" GROUP BY ").append(String.join(", ", groupBy)); }
    void renderHaving(StringBuilder s, List<Object> p) { if (having != null) { s.append(" HAVING "); Predicate.render(having, s, p); } }
    void renderWindow(StringBuilder s) {
        if (!windows.isEmpty()) { s.append(" WINDOW "); s.append(windows.stream().map(Window::render).collect(Collectors.joining(", "))); }
    }
    void renderOrder(StringBuilder s) {
        if (!orderBy.isEmpty()) {
            s.append(" ORDER BY ");
            for (int i = 0; i < orderBy.size(); i++) {
                if (i > 0) s.append(", "); s.append(orderBy.get(i).column()).append(' ').append(orderBy.get(i).direction());
            }
        }
    }
    void renderLimit(StringBuilder s, List<Object> p) {
        if (limit > 0) {
            if (offset > 0) { s.append(" LIMIT ?, ?"); p.add(offset); p.add(limit); }
            else { s.append(" LIMIT ?"); p.add(limit); }
        }
    }

    // -- GroupRef / SelectRef helpers --
    class GRImpl implements GroupRef<T> {
        @SuppressWarnings("unchecked") public GroupTable<T> group() { return (GroupTable<T>) proxy; }
        public Field<T, ?> key1() { return key(0); } public Field<T, ?> key2() { return key(1); } public Field<T, ?> key3() { return key(2); }
        Field<T, ?> key(int i) { return Field.of(groupBy.get(i), Object.class); }
    }

    class HGroupRef implements GroupRef<T> {
        final PredicateCollector c;
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
        public Field<T, ?> key1() { return key(0); } public Field<T, ?> key2() { return key(1); } public Field<T, ?> key3() { return key(2); }
        Field<T, ?> key(int i) { return Field.of(groupBy.get(i), Object.class); }
    }

    @SuppressWarnings("unchecked")
    class OTRef implements SelectRef<T> {
        final List<Order> specs;
        OTRef(List<Order> s) { this.specs = s; }
        public Field<T, ?> col(String alias) { return (Field<T,?>) (Object) new OTField(Field.of(alias, Object.class)); }
        class OTField extends Field<Object, Object> {
            OTField(Field<Object, Object> d) { super(d.name(), d.type()); }
            @Override public Order asc()  { Order o = super.asc(); specs.add(o); return o; }
            @Override public Order desc() { Order o = super.desc(); specs.add(o); return o; }
        }
    }

    record Join(String type, String table, String alias, Predicate on) {}
    record Cte(String name, SelectImpl<?> query, boolean recursive) {}
    record OrderCol(String outer, String alias, Set<String> innerCols) {
        OrderCol(String outer, String alias, String... cols) { this(outer, alias, new LinkedHashSet<>(Arrays.asList(cols))); }
        static OrderCol fromAgg(Aggregate a) { String o = a.renderForOuter(); String c = a.column();
            return new OrderCol(o, a.alias(), c != null && !"*".equals(c) ? Set.of(c) : Set.of()); }
    }
}
