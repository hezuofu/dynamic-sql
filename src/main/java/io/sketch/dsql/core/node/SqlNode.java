package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

@FunctionalInterface
public interface SqlNode {

    void apply(DynamicContext context);

    default SqlNode andThen(SqlNode after) {
        return context -> {
            this.apply(context);
            after.apply(context);
        };
    }

    default SqlNode compose(SqlNode before) {
        return context -> {
            before.apply(context);
            this.apply(context);
        };
    }

    static SqlNode empty() {
        return context -> {};
    }

    static SqlNode of(String text) {
        return new TextNode(text);
    }

    static SqlNode combine(SqlNode... nodes) {
        return context -> {
            for (SqlNode node : nodes) {
                node.apply(context);
            }
        };
    }
}