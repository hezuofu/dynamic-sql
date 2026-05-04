package io.sketch.dsql.functional;

import io.sketch.dsql.core.node.*;

public interface NodeVisitor<T> {

    T visit(TextNode node);

    T visit(IfNode node);

    T visit(ForEachNode node);

    T visit(WhereNode node);

    T visit(SetNode node);

    T visit(ChooseNode node);

    T visit(WhenNode node);

    T visit(OtherwiseNode node);

    T visit(TrimNode node);

    T visit(BindNode node);

    T visit(MixedNode node);

    T visit(WithNode node);

    T visit(UnionNode node);

    T visit(JoinNode node);
}