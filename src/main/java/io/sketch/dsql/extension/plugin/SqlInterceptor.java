package io.sketch.dsql.extension.plugin;

import io.sketch.dsql.core.context.DynamicContext;
import io.sketch.dsql.core.result.SqlResult;

import java.util.Map;

public interface SqlInterceptor {

    String beforeParse(String script, Map<String, Object> parameters);

    String afterParse(String script, Map<String, Object> parameters);

    void beforeExecute(DynamicContext context);

    SqlResult afterExecute(DynamicContext context, SqlResult result);
}