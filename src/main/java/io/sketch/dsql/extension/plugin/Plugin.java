package io.sketch.dsql.extension.plugin;

public interface Plugin {

    void init();

    void destroy();

    String getName();
}