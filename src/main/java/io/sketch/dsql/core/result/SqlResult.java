package io.sketch.dsql.core.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SqlResult {

    private final String sql;
    private final List<SqlParameter> parameters;
    private final Map<String, Object> metadata;

    private SqlResult(Builder builder) {
        this.sql = builder.sql;
        this.parameters = Collections.unmodifiableList(new ArrayList<>(builder.parameters));
        this.metadata = Collections.unmodifiableMap(new LinkedHashMap<>(builder.metadata));
    }

    public String getSql() {
        return sql;
    }

    public List<SqlParameter> getParameters() {
        return parameters;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public Map<String, Object> getParameterMap() {
        return parameters.stream()
                .collect(Collectors.toMap(
                        SqlParameter::getName,
                        SqlParameter::getValue,
                        (v1, v2) -> v1
                ));
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SqlResult sqlResult = (SqlResult) o;
        return Objects.equals(sql, sqlResult.sql) &&
                Objects.equals(parameters, sqlResult.parameters) &&
                Objects.equals(metadata, sqlResult.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sql, parameters, metadata);
    }

    @Override
    public String toString() {
        return "SqlResult{" +
                "sql='" + sql + '\'' +
                ", parameters=" + parameters +
                ", metadata=" + metadata +
                '}';
    }

    public static class Builder {
        private String sql;
        private final List<SqlParameter> parameters = new ArrayList<>();
        private final Map<String, Object> metadata = new LinkedHashMap<>();

        public Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Builder parameter(SqlParameter parameter) {
            this.parameters.add(parameter);
            return this;
        }

        public Builder parameter(String name, Object value) {
            this.parameters.add(SqlParameter.builder()
                    .name(name)
                    .value(value)
                    .build());
            return this;
        }

        public Builder parameters(List<SqlParameter> parameters) {
            this.parameters.addAll(parameters);
            return this;
        }

        public Builder metadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public SqlResult build() {
            return new SqlResult(this);
        }
    }
}