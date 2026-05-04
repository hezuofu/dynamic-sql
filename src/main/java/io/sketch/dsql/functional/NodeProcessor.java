package io.sketch.dsql.functional;

import io.sketch.dsql.core.context.DynamicContext;
import io.sketch.dsql.core.node.*;

import java.util.function.BiFunction;

public interface NodeProcessor {

    void process(TextNode node, DynamicContext context);

    void process(IfNode node, DynamicContext context);

    void process(ForEachNode node, DynamicContext context);

    void process(WhereNode node, DynamicContext context);

    void process(SetNode node, DynamicContext context);

    void process(ChooseNode node, DynamicContext context);

    void process(WhenNode node, DynamicContext context);

    void process(OtherwiseNode node, DynamicContext context);

    void process(TrimNode node, DynamicContext context);

    void process(BindNode node, DynamicContext context);

    void process(MixedNode node, DynamicContext context);

    void process(WithNode node, DynamicContext context);

    void process(UnionNode node, DynamicContext context);

    void process(JoinNode node, DynamicContext context);

    static NodeProcessor defaultProcessor() {
        return new DefaultNodeProcessor();
    }

    class DefaultNodeProcessor implements NodeProcessor {

        @Override
        public void process(TextNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(IfNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(ForEachNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(WhereNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(SetNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(ChooseNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(WhenNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(OtherwiseNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(TrimNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(BindNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(MixedNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(WithNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(UnionNode node, DynamicContext context) {
            node.apply(context);
        }

        @Override
        public void process(JoinNode node, DynamicContext context) {
            node.apply(context);
        }
    }
}