package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;
import io.sketch.dsql.core.expression.ExpressionEvaluator;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class ForEachNode implements SqlNode {

    private final String collection;
    private final String item;
    private final String index;
    private final String open;
    private final String close;
    private final String separator;
    private final SqlNode body;
    private final ExpressionEvaluator evaluator;

    public ForEachNode(String collection, String item, String index, 
                       String open, String close, String separator, 
                       SqlNode body, ExpressionEvaluator evaluator) {
        this.collection = collection;
        this.item = item;
        this.index = index;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.body = body;
        this.evaluator = evaluator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void apply(DynamicContext context) {
        Object collectionValue = evaluator.evaluate(collection, context.getAllVariables());
        
        if (!(collectionValue instanceof Collection<?>)) {
            return;
        }

        Collection<?> items = (Collection<?>) collectionValue;
        if (items.isEmpty()) {
            return;
        }

        if (open != null && !open.isEmpty()) {
            context.appendSql(open);
        }

        Iterator<?> iterator = items.iterator();
        int idx = 0;
        while (iterator.hasNext()) {
            Object itemValue = iterator.next();
            
            context.pushScope();
            context.setVariable(item, itemValue);
            if (index != null && !index.isEmpty()) {
                context.setVariable(index, idx);
            }
            
            body.apply(context);
            context.popScope();

            idx++;
            if (iterator.hasNext() && separator != null && !separator.isEmpty()) {
                context.appendSql(separator);
            }
        }

        if (close != null && !close.isEmpty()) {
            context.appendSql(close);
        }
    }

    public String getCollection() {
        return collection;
    }

    public String getItem() {
        return item;
    }

    public String getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForEachNode that = (ForEachNode) o;
        return Objects.equals(collection, that.collection) &&
                Objects.equals(item, that.item) &&
                Objects.equals(index, that.index) &&
                Objects.equals(open, that.open) &&
                Objects.equals(close, that.close) &&
                Objects.equals(separator, that.separator) &&
                Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(collection, item, index, open, close, separator, body);
    }

    @Override
    public String toString() {
        return "ForEachNode{" +
                "collection='" + collection + '\'' +
                ", item='" + item + '\'' +
                ", body=" + body +
                '}';
    }
}