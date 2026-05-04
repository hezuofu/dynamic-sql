package io.sketch.dsql.extension.listener;

import io.sketch.dsql.core.result.SqlResult;

import java.util.Map;

public interface SqlLifecycleListener {

    void onParseStart(String script);

    void onParseEnd(String script, long durationMs);

    void onExecuteStart(String script, Map<String, Object> parameters);

    void onExecuteEnd(SqlResult result, long durationMs);

    void onError(Exception e);
}