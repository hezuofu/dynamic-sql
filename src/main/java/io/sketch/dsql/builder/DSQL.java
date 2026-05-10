package io.sketch.dsql.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class DSQL {
    private DSQL() {}

    // SELECT cols FROM ...
    public static <T> Select<T> select(Function<T, List<Field<T,?>>> fn) {
        throw new UnsupportedOperationException("Need entity type — use DSQL.from().select() or DSQL.select(Class, fn)");
    }

    // SELECT cols FROM entity
    public static <T> Select<T> select(Class<T> entityType, Function<T, List<Field<T,?>>> fn) {
        SelectImpl<T> impl = new SelectImpl<>(entityType);
        for (Field<T,?> f : fn.apply(impl.proxy)) impl.cols.add(f.name());
        return impl;
    }

    // SELECT * FROM entity
    public static <T> Select<T> from(Class<T> entityType) {
        return new SelectImpl<>(entityType);
    }

    // WITH
    public static WithClause with(String name, Select<?> subquery) {
        return new WithClause(name, subquery, false);
    }

    public static WithClause withRecursive(String name, Select<?> subquery) {
        return new WithClause(name, subquery, true);
    }

    public static class WithClause {
        private final List<SelectImpl.Cte> ctes = new ArrayList<>();
        WithClause(String name, Select<?> sq, boolean rec) {
            if (sq instanceof SelectImpl<?> si) ctes.add(new SelectImpl.Cte(name, si, rec));
        }
        public WithClause and(String name, Select<?> sq) {
            if (sq instanceof SelectImpl<?> si) ctes.add(new SelectImpl.Cte(name, si, false)); return this;
        }
        public <T> Select<T> select(Class<T> entityType, Function<T, List<Field<T,?>>> fn) {
            SelectImpl<T> impl = new SelectImpl<>(entityType, EntityResolver.tableName(entityType), ctes);
            for (Field<T,?> f : fn.apply(impl.proxy)) impl.cols.add(f.name());
            return impl;
        }
        public <T> Select<T> from(Class<T> entityType) {
            return new SelectImpl<>(entityType, EntityResolver.tableName(entityType), ctes);
        }
    }

    // INSERT
    public static <T> Insert<T> insertInto(Class<T> entityType) {
        return new InsertImpl<>(entityType);
    }

    // UPDATE
    public static <T> Update<T> update(Class<T> entityType) {
        return new UpdateImpl<>(entityType);
    }

    // DELETE
    public static <T> Delete<T> deleteFrom(Class<T> entityType) {
        return new DeleteImpl<>(entityType);
    }
}
