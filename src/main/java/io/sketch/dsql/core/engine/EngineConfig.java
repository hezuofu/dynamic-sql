package io.sketch.dsql.core.engine;

import io.sketch.dsql.core.expression.ExpressionEvaluator;
import io.sketch.dsql.core.expression.MvelEvaluator;

import java.util.Objects;

public class EngineConfig {

    private ExpressionEvaluator expressionEvaluator;
    private String defaultParserFormat = "xml";
    private boolean cacheEnabled = true;
    private int cacheSize = 1000;
    private boolean validationEnabled = true;
    private boolean pluginEnabled = true;

    private EngineConfig() {
        this.expressionEvaluator = new MvelEvaluator();
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
        private final EngineConfig config = new EngineConfig();

        public Builder expressionEvaluator(ExpressionEvaluator evaluator) {
            config.expressionEvaluator = evaluator;
            return this;
        }

        public Builder defaultParserFormat(String format) {
            config.defaultParserFormat = format;
            return this;
        }

        public Builder cacheEnabled(boolean enabled) {
            config.cacheEnabled = enabled;
            return this;
        }

        public Builder cacheSize(int size) {
            config.cacheSize = size;
            return this;
        }

        public Builder validationEnabled(boolean enabled) {
            config.validationEnabled = enabled;
            return this;
        }

        public Builder pluginEnabled(boolean enabled) {
            config.pluginEnabled = enabled;
            return this;
        }

        public EngineConfig build() {
            return config;
        }
    }
}