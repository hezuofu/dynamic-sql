package io.sketch.dsql.core.result;

import java.util.Objects;

public class SqlParameter {

    private final String name;
    private final Object value;
    private final ParameterType type;

    private SqlParameter(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.type = builder.type != null ? builder.type : ParameterType.fromClass(
                value != null ? value.getClass() : Object.class);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public ParameterType getType() {
        return type;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlParameter that = (SqlParameter) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, type);
    }

    @Override
    public String toString() {
        return "SqlParameter{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", type=" + type +
                '}';
    }

    public static class Builder {
        private String name;
        private Object value;
        private ParameterType type;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder type(ParameterType type) {
            this.type = type;
            return this;
        }

        public SqlParameter build() {
            return new SqlParameter(this);
        }
    }
}