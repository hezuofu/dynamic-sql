package io.sketch.dsql.extension.cache;

import io.sketch.dsql.core.node.SqlNode;

public interface SqlCache {

    SqlNode get(String key);

    void put(String key, SqlNode value);

    void remove(String key);

    boolean contains(String key);

    void clear();

    int size();
}