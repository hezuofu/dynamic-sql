package io.sketch.dsql.builder;

public record Order(String column, Direction direction) {

    public enum Direction { ASC, DESC }

    public static Order asc(String col)  { return new Order(col, Direction.ASC); }
    public static Order desc(String col) { return new Order(col, Direction.DESC); }

    public Order { java.util.Objects.requireNonNull(column); if (direction == null) direction = Direction.ASC; }
}
