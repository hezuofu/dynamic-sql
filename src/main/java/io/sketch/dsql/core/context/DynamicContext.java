package io.sketch.dsql.core.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class DynamicContext {

    private final ContextStack contextStack = new ContextStack();
    private final StringBuilder sqlBuilder = new StringBuilder();
    private final List<Object> parameters = new ArrayList<>();
    private final AtomicInteger parameterIndex = new AtomicInteger(0);

    public DynamicContext() {
        contextStack.push();
    }

    public DynamicContext(Map<String, Object> initialVariables) {
        this();
        initialVariables.forEach(this::setVariable);
    }

    public void setVariable(String name, Object value) {
        contextStack.setVariable(name, value);
    }

    public Object getVariable(String name) {
        return contextStack.getVariable(name).orElse(null);
    }

    public boolean containsVariable(String name) {
        return contextStack.containsVariable(name);
    }

    public void pushScope() {
        contextStack.push();
    }

    public void popScope() {
        contextStack.pop();
    }

    public void appendSql(String sql) {
        sqlBuilder.append(sql);
    }

    public void appendSql(char c) {
        sqlBuilder.append(c);
    }

    public String getSql() {
        return sqlBuilder.toString();
    }

    public int getSqlLength() {
        return sqlBuilder.length();
    }

    public void setSqlLength(int length) {
        sqlBuilder.setLength(length);
    }

    public void addParameter(Object value) {
        parameters.add(value);
    }

    public void addParameter(String name, Object value) {
        parameters.add(value);
        setVariable(name, value);
    }

    public List<Object> getParameters() {
        return new ArrayList<>(parameters);
    }

    public int getParameterCount() {
        return parameters.size();
    }

    public int nextParameterIndex() {
        return parameterIndex.incrementAndGet();
    }

    public Map<String, Object> getAllVariables() {
        return contextStack.getAllVariables();
    }

    public void clear() {
        sqlBuilder.setLength(0);
        parameters.clear();
        parameterIndex.set(0);
        contextStack.clear();
        contextStack.push();
    }

    public DynamicContext copy() {
        DynamicContext copy = new DynamicContext();
        copy.sqlBuilder.append(this.sqlBuilder);
        copy.parameters.addAll(this.parameters);
        copy.parameterIndex.set(this.parameterIndex.get());
        copy.contextStack.clear();
        for (Map<String, ContextVariable> frame : this.contextStack.getFrames()) {
            copy.contextStack.push();
            frame.forEach((k, v) -> copy.contextStack.setVariable(k, v.getValue(), v.isModifiable()));
        }
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicContext that = (DynamicContext) o;
        return Objects.equals(sqlBuilder.toString(), that.sqlBuilder.toString()) &&
                Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sqlBuilder.toString(), parameters);
    }

    @Override
    public String toString() {
        return "DynamicContext{" +
                "sql='" + getSql() + '\'' +
                ", parameters=" + parameters +
                ", variables=" + getAllVariables() +
                '}';
    }
}