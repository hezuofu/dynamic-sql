package io.sketch.dsql.builder;

import java.util.List;
import java.util.Objects;

public class Field<T, V> {

    private final String name;
    private final Class<V> type;

    public Field(String name, Class<V> type) {
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
    }

    public static <T, V> Field<T, V> of(String name, Class<V> type) { return new Field<>(name, type); }

    public String name() { return name; }
    public Class<V> type() { return type; }

    // Predicate
    public Predicate eq(V v)     { return pred(Predicate.Op.EQ, v); }
    public Predicate neq(V v)    { return pred(Predicate.Op.NEQ, v); }
    public Predicate gt(V v)     { return pred(Predicate.Op.GT, v); }
    public Predicate gte(V v)    { return pred(Predicate.Op.GTE, v); }
    public Predicate lt(V v)     { return pred(Predicate.Op.LT, v); }
    public Predicate lte(V v)    { return pred(Predicate.Op.LTE, v); }
    public Predicate like(V v)   { return pred(Predicate.Op.LIKE, v); }
    @SuppressWarnings("unchecked")
    public Predicate in(V... v)    { return pred(Predicate.Op.IN, List.of(v)); }
    @SuppressWarnings("unchecked")
    public Predicate notIn(V... v) { return pred(Predicate.Op.NOT_IN, List.of(v)); }
    public Predicate between(V a, V b)                         { return pred(Predicate.Op.BETWEEN, List.of(a, b)); }
    public Predicate isNull()                                  { return pred(Predicate.Op.IS_NULL, List.of()); }
    public Predicate isNotNull()                               { return pred(Predicate.Op.IS_NOT_NULL, List.of()); }

    // Aggregate
    public Aggregate max()   { return Aggregate.max(name); }
    public Aggregate min()   { return Aggregate.min(name); }
    public Aggregate sum()   { return Aggregate.sum(name); }
    public Aggregate avg()   { return Aggregate.avg(name); }
    public Aggregate count() { return Aggregate.count(name); }

    // Order
    public Order asc()  { return Order.asc(name); }
    public Order desc() { return Order.desc(name); }

    private Predicate pred(Predicate.Op op, Object v) { return pred(op, List.of(v)); }
    private Predicate pred(Predicate.Op op, List<Object> vs) { return new Predicate.ColumnPredicate(this, op, vs); }

    @Override public boolean equals(Object o) { return o instanceof Field<?,?> f && name.equals(f.name); }
    @Override public int hashCode() { return name.hashCode(); }
    @Override public String toString() { return name; }
}
