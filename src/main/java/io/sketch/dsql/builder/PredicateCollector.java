package io.sketch.dsql.builder;

final class PredicateCollector {
    private Predicate current;

    void add(Predicate p) {
        if (current == null) current = p;
        else current = new Predicate.Junction(current, Predicate.Logic.AND, p);
    }

    Predicate build() { return current; }
}
