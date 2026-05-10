package io.sketch.dsql.builder;

import java.util.ArrayList;
import java.util.List;

public class Window {

    private final String name;
    private final List<String> partitionBy = new ArrayList<>();
    private final List<Order> orderBy = new ArrayList<>();

    Window(String name) { this.name = name; }

    public static Window named(String name) { return new Window(name); }

    public Window partitionBy(String... cols) { partitionBy.addAll(List.of(cols)); return this; }
    public Window orderBy(Order... orders) { orderBy.addAll(List.of(orders)); return this; }

    public String name() { return name; }
    public List<String> partitionBy() { return partitionBy; }
    public List<Order> orderBy() { return orderBy; }

    // Window functions
    public static Aggregate rowNumber()    { return new Aggregate("ROW_NUMBER()", null, null); }
    public static Aggregate rank()          { return new Aggregate("RANK()", null, null); }
    public static Aggregate denseRank()     { return new Aggregate("DENSE_RANK()", null, null); }

    String render() {
        StringBuilder s = new StringBuilder(name);
        if (!partitionBy.isEmpty()) s.append(" PARTITION BY ").append(String.join(", ", partitionBy));
        if (!orderBy.isEmpty()) {
            s.append(" ORDER BY ");
            for (int i = 0; i < orderBy.size(); i++) {
                if (i > 0) s.append(", ");
                s.append(orderBy.get(i).column()).append(' ').append(orderBy.get(i).direction());
            }
        }
        return s.toString();
    }
}
