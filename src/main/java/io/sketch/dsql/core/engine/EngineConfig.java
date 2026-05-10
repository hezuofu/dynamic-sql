package io.sketch.dsql.core.engine;

import io.sketch.dsql.core.expression.ExpressionEvaluator;
import io.sketch.dsql.core.expression.MvelEvaluator;

import java.util.Objects;

public class EngineConfig {

    private final ExpressionEvaluator expressionEvaluator;
    private final String defaultParserFormat;
    private final boolean cacheEnabled;
    private final int cacheSize;
    private final boolean validationEnabled;
    private final boolean pluginEnabled;

    private EngineConfig(Builder builder) {
        this.expressionEvaluator = builder.expressionEvaluator != null
                ? builder.expressionEvaluator : new MvelEvaluator();
        this.defaultParserFormat = builder.defaultParserFormat != null
                ? builder.defaultParserFormat : "xml";
        this.cacheEnabled = builder.cacheEnabled;
        this.cacheSize = builder.cacheSize;
        this.validationEnabled = builder.validationEnabled;
        this.pluginEnabled = builder.pluginEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ExpressionEvaluator getExpressionEvaluator() {
        return expressionEvaluator;
    }

    public String getDefaultParserFormat() {
        return defaultParserFormat;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public boolean isValidationEnabled() {
        return validationEnabled;
    }

    public boolean isPluginEnabled() {
        return pluginEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EngineConfig that = (EngineConfig) o;
        return cacheEnabled == that.cacheEnabled &&
                cacheSize == that.cacheSize &&
                validationEnabled == that.validationEnabled &&
                pluginEnabled == that.pluginEnabled &&
                Objects.equals(expressionEvaluator, that.expressionEvaluator) &&
                Objects.equals(defaultParserFormat, that.defaultParserFormat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressionEvaluator, defaultParserFormat, cacheEnabled, cacheSize, validationEnabled, pluginEnabled);
    }

    public static class Builder {
        private ExpressionEvaluator expressionEvaluator;
        private String defaultParserFormat;
        private boolean cacheEnabled = true;
        private int cacheSize = 1000;
        private boolean validationEnabled = true;
        private boolean pluginEnabled = true;

        public Builder expressionEvaluator(ExpressionEvaluator evaluator) {
            this.expressionEvaluator = evaluator;
            return this;
        }

        public Builder defaultParserFormat(String format) {
            this.defaultParserFormat = format;
            return this;
        }

        public Builder cacheEnabled(boolean enabled) {
            this.cacheEnabled = enabled;
            return this;
        }

        public Builder cacheSize(int size) {
            this.cacheSize = size;
            return this;
        }

        public Builder validationEnabled(boolean enabled) {
            this.validationEnabled = enabled;
            return this;
        }

        public Builder pluginEnabled(boolean enabled) {
            this.pluginEnabled = enabled;
            return this;
        }

        public EngineConfig build() {
            return new EngineConfig(this);
        }
    }
}
