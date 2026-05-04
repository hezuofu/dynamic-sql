package io.sketch.dsql.extension.listener;

import io.sketch.dsql.core.node.SqlNode;

public interface ParseEventListener {

    void onNodeCreated(SqlNode node);

    void onParseComplete(SqlNode rootNode);

    void onParseError(Exception e);
}