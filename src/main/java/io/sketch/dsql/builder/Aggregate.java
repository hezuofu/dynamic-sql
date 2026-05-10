package io.sketch.dsql.builder;

public class Aggregate {

    private final String function;
    private final String column;
    private final String alias;

    Aggregate(String function, String column, String alias) {
        this.function = function; this.column = column; this.alias = alias;
    }

    public static Aggregate countStar() { return new Aggregate("COUNT(*)", null, null); }
    public static Aggregate count(String col) { return new Aggregate("COUNT(" + col + ")", col, null); }
    public static Aggregate sum(String col)   { return new Aggregate("SUM(" + col + ")", col, null); }
    public static Aggregate avg(String col)   { return new Aggregate("AVG(" + col + ")", col, null); }
    public static Aggregate max(String col)   { return new Aggregate("MAX(" + col + ")", col, null); }
    public static Aggregate min(String col)   { return new Aggregate("MIN(" + col + ")", col, null); }

    public Aggregate as(String alias) { return new Aggregate(function, column, alias); }

    public String function() { return function; }
    public String column() { return column; }
    public String alias() { return alias; }

    String renderForOuter() { return column != null ? function.replace(column, "t1." + column) : function; }
}
