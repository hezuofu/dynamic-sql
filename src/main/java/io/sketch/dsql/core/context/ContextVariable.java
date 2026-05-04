package io.sketch.dsql.core.context;

import java.util.Objects;

public class ContextVariable {

    private final String name;
    private final Object value;
    private final boolean modifiable;

    private ContextVariable(Builder builder) {
        this.name = builder.name;
        this.value = builder.value;
        this.modifiable = builder.modifiable;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isModifiable() {
        return modifiable;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContextVariable that = (ContextVariable) o;
        return modifiable == that.modifiable &&
                Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value, modifiable);
    }

    @Override
    public String toString() {
        return "ContextVariable{" +
                "name='" + name + '\'' +
                ", value=" + value +
                ", modifiable=" + modifiable +
                '}';
    }

    public static class Builder {
        private String name;
        private Object value;
        private boolean modifiable = true;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        public Builder modifiable(boolean modifiable) {
            this.modifiable = modifiable;
            return this;
        }

        public ContextVariable build() {
            return new ContextVariable(this);
        }
    }
}