package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChooseNode implements SqlNode {

    private final List<WhenNode> whenNodes;
    private final OtherwiseNode otherwiseNode;

    public ChooseNode(List<WhenNode> whenNodes, OtherwiseNode otherwiseNode) {
        this.whenNodes = whenNodes != null ? whenNodes : new ArrayList<>();
        this.otherwiseNode = otherwiseNode;
    }

    @Override
    public void apply(DynamicContext context) {
        for (WhenNode whenNode : whenNodes) {
            if (whenNode.evaluateTest(context)) {
                whenNode.apply(context);
                return;
            }
        }
        
        if (otherwiseNode != null) {
            otherwiseNode.apply(context);
        }
    }

    public List<WhenNode> getWhenNodes() {
        return new ArrayList<>(whenNodes);
    }

    public OtherwiseNode getOtherwiseNode() {
        return otherwiseNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChooseNode that = (ChooseNode) o;
        return Objects.equals(whenNodes, that.whenNodes) &&
                Objects.equals(otherwiseNode, that.otherwiseNode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(whenNodes, otherwiseNode);
    }

    @Override
    public String toString() {
        return "ChooseNode{" +
                "whenNodes=" + whenNodes +
                ", otherwiseNode=" + otherwiseNode +
                '}';
    }
}