package io.sketch.dsql.core.engine;

import io.sketch.dsql.core.result.SqlResult;

import java.util.Map;

public interface SqlEngine {

    SqlResult execute(String script, Map<String, Object> parameters);

    SqlResult execute(String script, Map<String, Object> parameters, String format);

    void reload();

    EngineConfig getConfig();
}