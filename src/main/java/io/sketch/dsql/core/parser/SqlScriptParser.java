package io.sketch.dsql.core.parser;

import io.sketch.dsql.core.node.SqlNode;

@FunctionalInterface
public interface SqlScriptParser {

    SqlNode parse(String script);

    default boolean supports(String format) {
        return false;
    }
}