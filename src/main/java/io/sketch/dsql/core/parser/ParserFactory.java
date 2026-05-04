package io.sketch.dsql.core.parser;

import io.sketch.dsql.core.expression.ExpressionEvaluator;
import io.sketch.dsql.core.expression.MvelEvaluator;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public class ParserFactory {

    private static final Map<String, SqlScriptParser> parsers = new HashMap<>();
    private static volatile ParserFactory instance;

    private ParserFactory() {
    }

    public static ParserFactory getInstance() {
        if (instance == null) {
            synchronized (ParserFactory.class) {
                if (instance == null) {
                    instance = new ParserFactory();
                    instance.loadParsers();
                }
            }
        }
        return instance;
    }

    private void loadParsers() {
        ServiceLoader<SqlScriptParser> loader = ServiceLoader.load(SqlScriptParser.class);
        loader.forEach(parser -> {
            if (parser instanceof XmlSqlScriptParser) {
                parsers.put("xml", parser);
            } else if (parser instanceof JsonSqlScriptParser) {
                parsers.put("json", parser);
            }
        });
    }

    public SqlScriptParser getParser(String format) {
        SqlScriptParser parser = parsers.get(format.toLowerCase());
        if (parser != null) {
            return parser;
        }
        
        ExpressionEvaluator evaluator = new MvelEvaluator();
        return switch (format.toLowerCase()) {
            case "xml" -> {
                XmlSqlScriptParser xmlParser = new XmlSqlScriptParser(evaluator);
                parsers.put("xml", xmlParser);
                yield xmlParser;
            }
            case "json" -> {
                JsonSqlScriptParser jsonParser = new JsonSqlScriptParser(evaluator);
                parsers.put("json", jsonParser);
                yield jsonParser;
            }
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    public void registerParser(String format, SqlScriptParser parser) {
        parsers.put(format.toLowerCase(), parser);
    }

    public boolean hasParser(String format) {
        return parsers.containsKey(format.toLowerCase());
    }
}