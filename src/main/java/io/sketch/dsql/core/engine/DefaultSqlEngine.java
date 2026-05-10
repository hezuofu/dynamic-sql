package io.sketch.dsql.core.engine;

import io.sketch.dsql.core.context.DynamicContext;
import io.sketch.dsql.core.node.SqlNode;
import io.sketch.dsql.core.parser.ParserFactory;
import io.sketch.dsql.core.parser.SqlScriptParser;
import io.sketch.dsql.core.result.SqlParameter;
import io.sketch.dsql.core.result.SqlResult;
import io.sketch.dsql.core.validator.ParameterValidator;
import io.sketch.dsql.core.validator.SyntaxValidator;
import io.sketch.dsql.core.validator.ValidationResult;
import io.sketch.dsql.exception.DynamicSqlException;
import io.sketch.dsql.exception.SqlValidateException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class DefaultSqlEngine implements SqlEngine {

    private final EngineConfig config;
    private final ParserFactory parserFactory;
    private final ConcurrentHashMap<String, SqlNode> scriptCache;
    private final SyntaxValidator syntaxValidator;
    private final ParameterValidator parameterValidator;

    public DefaultSqlEngine() {
        this(EngineConfig.builder().build());
    }

    public DefaultSqlEngine(EngineConfig config) {
        this.config = config;
        this.parserFactory = ParserFactory.getInstance();
        this.scriptCache = new ConcurrentHashMap<>(config.getCacheSize());
        this.syntaxValidator = new SyntaxValidator();
        this.parameterValidator = new ParameterValidator();
    }

    @Override
    public SqlResult execute(String script, Map<String, Object> parameters) {
        return execute(script, parameters, config.getDefaultParserFormat());
    }

    @Override
    public SqlResult execute(String script, Map<String, Object> parameters, String format) {
        if (config.isValidationEnabled()) {
            ValidationResult validation = syntaxValidator.validate(script);
            if (validation.hasErrors()) {
                String errors = validation.getErrors().stream()
                        .map(e -> e.getField() + ": " + e.getMessage())
                        .collect(Collectors.joining(", "));
                throw new SqlValidateException("Script validation failed: " + errors);
            }
        }

        SqlNode node = parseScript(script, format);
        DynamicContext context = new DynamicContext(parameters);
        
        node.apply(context);

        AtomicInteger index = new AtomicInteger(0);
        List<SqlParameter> sqlParameters = context.getParameters().stream()
                .map(value -> SqlParameter.builder()
                        .name("param" + index.incrementAndGet())
                        .value(value)
                        .build())
                .collect(Collectors.toList());

        return SqlResult.builder()
                .sql(context.getSql())
                .parameters(sqlParameters)
                .metadata(Map.of("scriptHash", String.valueOf(script.hashCode())))
                .build();
    }

    private SqlNode parseScript(String script, String format) {
        String cacheKey = script + ":" + format;
        
        if (config.isCacheEnabled()) {
            SqlNode cached = scriptCache.get(cacheKey);
            if (cached != null) {
                return cached;
            }
        }

        SqlScriptParser parser = parserFactory.getParser(format, config.getExpressionEvaluator());
        SqlNode node = parser.parse(script);

        if (config.isCacheEnabled()) {
            scriptCache.put(cacheKey, node);
        }

        return node;
    }

    @Override
    public void reload() {
        scriptCache.clear();
    }

    @Override
    public EngineConfig getConfig() {
        return config;
    }
}