package io.sketch.dsql.core.parser;

import io.sketch.dsql.core.expression.ExpressionEvaluator;
import io.sketch.dsql.core.expression.MvelEvaluator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParserFactory {

    private final Map<String, SqlScriptParser> registeredParsers = new ConcurrentHashMap<>();
    private static volatile ParserFactory instance;

    private ParserFactory() {
    }

    public static ParserFactory getInstance() {
        if (instance == null) {
            synchronized (ParserFactory.class) {
                if (instance == null) {
                    instance = new ParserFactory();
                }
            }
        }
        return instance;
    }

    public SqlScriptParser getParser(String format) {
        return getParser(format, new MvelEvaluator());
    }

    public SqlScriptParser getParser(String format, ExpressionEvaluator evaluator) {
        SqlScriptParser registered = registeredParsers.get(format.toLowerCase());
        if (registered != null) {
            return registered;
        }

        return switch (format.toLowerCase()) {
            case "xml" -> new XmlSqlScriptParser(evaluator);
            case "json" -> new JsonSqlScriptParser(evaluator);
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    public void registerParser(String format, SqlScriptParser parser) {
        registeredParsers.put(format.toLowerCase(), parser);
    }

    public boolean hasParser(String format) {
        return registeredParsers.containsKey(format.toLowerCase())
                || "xml".equalsIgnoreCase(format)
                || "json".equalsIgnoreCase(format);
    }
}
