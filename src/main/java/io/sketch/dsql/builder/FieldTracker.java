package io.sketch.dsql.builder;

import java.util.List;

final class FieldTracker<T, V> extends Field<T, V> {

    private final PredicateCollector collector;

    FieldTracker(Field<T, V> delegate, PredicateCollector collector) {
        super(delegate.name(), delegate.type());
        this.collector = collector;
    }

    @Override public Predicate eq(V v)     { return track(super.eq(v)); }
    @Override public Predicate neq(V v)    { return track(super.neq(v)); }
    @Override public Predicate gt(V v)     { return track(super.gt(v)); }
    @Override public Predicate gte(V v)    { return track(super.gte(v)); }
    @Override public Predicate lt(V v)     { return track(super.lt(v)); }
    @Override public Predicate lte(V v)    { return track(super.lte(v)); }
    @Override public Predicate like(V v)   { return track(super.like(v)); }
    @Override @SuppressWarnings("unchecked")
    public Predicate in(V... v)    { return track(super.in(v)); }
    @Override @SuppressWarnings("unchecked")
    public Predicate notIn(V... v) { return track(super.notIn(v)); }
    @Override public Predicate between(V a, V b) { return track(super.between(a, b)); }
    @Override public Predicate isNull()          { return track(super.isNull()); }
    @Override public Predicate isNotNull()        { return track(super.isNotNull()); }

    private Predicate track(Predicate p) { collector.add(p); return p; }
}
