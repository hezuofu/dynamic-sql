package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MixedNode implements SqlNode {

    private final List<SqlNode> children;

    public MixedNode(List<SqlNode> children) {
        this.children = children != null ? children : new ArrayList<>();
    }

    @Override
    public void apply(DynamicContext context) {
        for (SqlNode child : children) {
            child.apply(context);
        }
    }

    public List<SqlNode> getChildren() {
        return new ArrayList<>(children);
    }

    public void addChild(SqlNode child) {
        children.add(child);
    }

    public void addChildren(List<SqlNode> children) {
        this.children.addAll(children);
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    public int size() {
        return children.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MixedNode mixedNode = (MixedNode) o;
        return Objects.equals(children, mixedNode.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    @Override
    public String toString() {
        return "MixedNode{" +
                "children=" + children +
                '}';
    }
}