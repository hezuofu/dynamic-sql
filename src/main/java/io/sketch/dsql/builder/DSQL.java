package io.sketch.dsql.builder;

public final class DSQL {
    private DSQL() {}

    public static <T> Select<T> from(Class<T> entityType) {
        return new SelectContext<>(entityType).new QImpl();
    }

    public static <T> Insert<T> insertInto(Class<T> entityType) {
        return new InsertImpl<>(entityType);
    }

    public static <T> Update<T> update(Class<T> entityType) {
        return new UpdateImpl<>(entityType);
    }

    public static <T> Delete<T> deleteFrom(Class<T> entityType) {
        return new DeleteImpl<>(entityType);
    }
}
