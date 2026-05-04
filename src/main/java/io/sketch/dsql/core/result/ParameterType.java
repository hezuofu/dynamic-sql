package io.sketch.dsql.core.result;

public enum ParameterType {
    STRING(String.class),
    INTEGER(Integer.class),
    LONG(Long.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    DATE(java.util.Date.class),
    OBJECT(Object.class);

    private final Class<?> type;

    ParameterType(Class<?> type) {
        this.type = type;
    }

    public Class<?> getType() {
        return type;
    }

    public static ParameterType fromClass(Class<?> clazz) {
        for (ParameterType type : values()) {
            if (type.type.isAssignableFrom(clazz)) {
                return type;
            }
        }
        return OBJECT;
    }
}