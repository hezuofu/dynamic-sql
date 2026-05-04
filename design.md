dynamic-sql/
├── pom.xml
├── src/main/java/io/sketch/dsql/
│   ├── core/
│   │   ├── engine/
│   │   │   ├── SqlEngine.java
│   │   │   ├── DefaultSqlEngine.java
│   │   │   └── EngineConfig.java
│   │   ├── parser/
│   │   │   ├── SqlScriptParser.java
│   │   │   ├── XmlSqlScriptParser.java
│   │   │   ├── JsonSqlScriptParser.java
│   │   │   └── ParserFactory.java
│   │   ├── node/
│   │   │   ├── SqlNode.java
│   │   │   ├── NodeType.java
│   │   │   ├── TextNode.java
│   │   │   ├── IfNode.java
│   │   │   ├── ForEachNode.java
│   │   │   ├── WhereNode.java
│   │   │   ├── SetNode.java
│   │   │   ├── ChooseNode.java
│   │   │   ├── WhenNode.java
│   │   │   ├── OtherwiseNode.java
│   │   │   ├── TrimNode.java
│   │   │   ├── BindNode.java
│   │   │   └── MixedNode.java
│   │   ├── context/
│   │   │   ├── DynamicContext.java
│   │   │   ├── ContextVariable.java
│   │   │   └── ContextStack.java
│   │   ├── expression/
│   │   │   ├── ExpressionEvaluator.java
│   │   │   ├── MvelEvaluator.java
│   │   │   ├── SpelEvaluator.java
│   │   │   └── ExpressionFunction.java
│   │   ├── validator/
│   │   │   ├── SqlValidator.java
│   │   │   ├── SyntaxValidator.java
│   │   │   ├── ParameterValidator.java
│   │   │   └── ValidationResult.java
│   │   └── result/
│   │       ├── SqlResult.java
│   │       ├── SqlParameter.java
│   │       └── ParameterType.java
│   ├── functional/
│   │   ├── SqlFunction.java
│   │   ├── NodeProcessor.java
│   │   ├── ExpressionFunctionRegistry.java
│   │   └── NodeVisitor.java
│   ├── extension/
│   │   ├── plugin/
│   │   │   ├── Plugin.java
│   │   │   ├── PluginChain.java
│   │   │   └── SqlInterceptor.java
│   │   ├── cache/
│   │   │   ├── SqlCache.java
│   │   │   ├── DefaultSqlCache.java
│   │   │   └── CacheKeyGenerator.java
│   │   └── listener/
│   │       ├── SqlLifecycleListener.java
│   │       └── ParseEventListener.java
│   └── exception/
│       ├── SqlParseException.java
│       ├── SqlValidateException.java
│       ├── ExpressionException.java
│       └── DynamicSqlException.java
├── src/test/java/com/dsql/
│   ├── parser/
│   ├── engine/
│   └── resources/
│       └── test-scripts/
└── README.md