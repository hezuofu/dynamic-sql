package io.sketch.dsql.core.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sketch.dsql.core.expression.ExpressionEvaluator;
import io.sketch.dsql.core.node.*;
import io.sketch.dsql.exception.SqlParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class JsonSqlScriptParser implements SqlScriptParser {

    private final ExpressionEvaluator evaluator;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonSqlScriptParser(ExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public SqlNode parse(String script) {
        try {
            JsonNode root = objectMapper.readTree(script);
            return parseJsonNode(root);
        } catch (SqlParseException e) {
            throw e;
        } catch (Exception e) {
            throw new SqlParseException("Failed to parse JSON script", e);
        }
    }

    @Override
    public boolean supports(String format) {
        return "json".equalsIgnoreCase(format);
    }

    private SqlNode parseJsonNode(JsonNode node) {
        if (node.isTextual()) {
            return new TextNode(node.asText());
        }

        if (node.isArray()) {
            List<SqlNode> children = new ArrayList<>();
            for (JsonNode child : node) {
                children.add(parseJsonNode(child));
            }
            return new MixedNode(children);
        }

        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String nodeType = field.getKey();
                JsonNode content = field.getValue();
                return parseNodeByType(nodeType, content);
            }
        }

        throw new SqlParseException("Invalid JSON node type");
    }

    private SqlNode parseNodeByType(String nodeType, JsonNode content) {
        return switch (nodeType) {
            case "if" -> parseIfNode(content);
            case "forEach", "foreach" -> parseForEachNode(content);
            case "where" -> parseWhereNode(content);
            case "set" -> parseSetNode(content);
            case "choose" -> parseChooseNode(content);
            case "text" -> new TextNode(content.asText());
            case "trim" -> parseTrimNode(content);
            case "bind" -> parseBindNode(content);
            case "with" -> parseWithNode(content);
            case "union" -> parseUnionNode(content);
            case "join" -> parseJoinNode(content);
            default -> throw new SqlParseException("Unknown node type: " + nodeType);
        };
    }

    private IfNode parseIfNode(JsonNode content) {
        String test = getText(content, "test");
        JsonNode body = content.get("body");
        return new IfNode(test, parseJsonNode(body), evaluator);
    }

    private ForEachNode parseForEachNode(JsonNode content) {
        String collection = getText(content, "collection");
        String item = getText(content, "item");
        String index = getText(content, "index");
        String open = getText(content, "open");
        String close = getText(content, "close");
        String separator = getText(content, "separator");
        JsonNode body = content.get("body");
        return new ForEachNode(collection, item, index, open, close, separator, parseJsonNode(body), evaluator);
    }

    private WhereNode parseWhereNode(JsonNode content) {
        return new WhereNode(parseJsonNode(content));
    }

    private SetNode parseSetNode(JsonNode content) {
        return new SetNode(parseJsonNode(content));
    }

    private ChooseNode parseChooseNode(JsonNode content) {
        List<WhenNode> whenNodes = new ArrayList<>();
        OtherwiseNode otherwiseNode = null;

        JsonNode whens = content.get("when");
        if (whens != null && whens.isArray()) {
            for (JsonNode when : whens) {
                whenNodes.add(parseWhenNode(when));
            }
        }

        JsonNode otherwise = content.get("otherwise");
        if (otherwise != null) {
            otherwiseNode = parseOtherwiseNode(otherwise);
        }

        return new ChooseNode(whenNodes, otherwiseNode);
    }

    private WhenNode parseWhenNode(JsonNode content) {
        String test = getText(content, "test");
        JsonNode body = content.get("body");
        return new WhenNode(test, parseJsonNode(body), evaluator);
    }

    private OtherwiseNode parseOtherwiseNode(JsonNode content) {
        return new OtherwiseNode(parseJsonNode(content));
    }

    private TrimNode parseTrimNode(JsonNode content) {
        String prefixesToRemove = getText(content, "prefixesToRemove");
        String suffixesToRemove = getText(content, "suffixesToRemove");
        String prefix = getText(content, "prefix");
        String suffix = getText(content, "suffix");
        JsonNode body = content.get("body");
        return new TrimNode(parseJsonNode(body), prefixesToRemove, suffixesToRemove, prefix, suffix);
    }

    private BindNode parseBindNode(JsonNode content) {
        String name = getText(content, "name");
        String expression = getText(content, "value");
        return new BindNode(name, expression, evaluator);
    }

    private WithNode parseWithNode(JsonNode content) {
        String alias = getText(content, "alias");
        boolean recursive = content.has("recursive") && content.get("recursive").asBoolean();
        JsonNode subquery = content.get("subquery");
        JsonNode body = content.get("body");
        return new WithNode(alias, parseJsonNode(subquery), recursive, parseJsonNode(body));
    }

    private UnionNode parseUnionNode(JsonNode content) {
        boolean all = content.has("all") && content.get("all").asBoolean();
        boolean distinct = content.has("distinct") && content.get("distinct").asBoolean();
        JsonNode queries = content.get("queries");
        
        List<SqlNode> queryList = new ArrayList<>();
        if (queries != null && queries.isArray()) {
            for (JsonNode query : queries) {
                queryList.add(parseJsonNode(query));
            }
        }
        
        return new UnionNode(queryList, all, distinct);
    }

    private JoinNode parseJoinNode(JsonNode content) {
        String type = getText(content, "type");
        String table = getText(content, "table");
        String alias = getText(content, "alias");
        String onCondition = getText(content, "on");
        
        JoinNode.JoinType joinType = JoinNode.JoinType.INNER;
        if (type != null && !type.isEmpty()) {
            try {
                joinType = JoinNode.JoinType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new SqlParseException("Invalid join type: " + type);
            }
        }
        
        return new JoinNode(joinType, table, alias, onCondition);
    }

    private String getText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value != null && !value.isNull() ? value.asText() : null;
    }
}