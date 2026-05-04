package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.Objects;

public class TextNode implements SqlNode {

    private final String text;

    public TextNode(String text) {
        this.text = text != null ? text : "";
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(text);
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextNode textNode = (TextNode) o;
        return Objects.equals(text, textNode.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "TextNode{" +
                "text='" + text + '\'' +
                '}';
    }
}