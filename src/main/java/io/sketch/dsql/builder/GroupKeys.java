package io.sketch.dsql.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GroupKeys {
    private final List<String> columns;

    GroupKeys(List<String> columns) { this.columns = Collections.unmodifiableList(columns); }

    public static GroupKeys of(String col, String... more) {
        List<String> l = new ArrayList<>(); l.add(col); l.addAll(Arrays.asList(more)); return new GroupKeys(l);
    }

    @SafeVarargs
    public static <T> GroupKeys of(Field<T, ?> col, Field<T, ?>... more) {
        List<String> l = new ArrayList<>(); l.add(col.name());
        for (Field<T, ?> c : more) l.add(c.name()); return new GroupKeys(l);
    }

    public List<String> columns() { return columns; }
}
