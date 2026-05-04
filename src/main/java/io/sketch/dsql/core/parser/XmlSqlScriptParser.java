package io.sketch.dsql.core.parser;

import io.sketch.dsql.core.expression.ExpressionEvaluator;
import io.sketch.dsql.core.node.*;
import io.sketch.dsql.exception.SqlParseException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class XmlSqlScriptParser implements SqlScriptParser {

    private final ExpressionEvaluator evaluator;

    public XmlSqlScriptParser(ExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    public SqlNode parse(String script) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(script)));
            doc.getDocumentElement().normalize();
            
            return parseNode(doc.getDocumentElement());
        } catch (SqlParseException e) {
            throw e;
        } catch (Exception e) {
            throw new SqlParseException("Failed to parse XML script", e);
        }
    }

    @Override
    public boolean supports(String format) {
        return "xml".equalsIgnoreCase(format);
    }

    private SqlNode parseNode(Node node) {
        NodeList children = node.getChildNodes();
        List<SqlNode> childNodes = new ArrayList<>();

        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE) {
                String text = child.getNodeValue().trim();
                if (!text.isEmpty()) {
                    childNodes.add(new TextNode(text));
                }
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                childNodes.add(parseElement((Element) child));
            }
        }

        if (childNodes.size() == 1) {
            return childNodes.get(0);
        }
        return new MixedNode(childNodes);
    }

    private SqlNode parseElement(Element element) {
        String tagName = element.getTagName();
        
        switch (tagName) {
            case "if":
                return parseIfNode(element);
            case "forEach":
            case "foreach":
                return parseForEachNode(element);
            case "where":
                return parseWhereNode(element);
            case "set":
                return parseSetNode(element);
            case "choose":
                return parseChooseNode(element);
            case "when":
                return parseWhenNode(element);
            case "otherwise":
                return parseOtherwiseNode(element);
            case "trim":
                return parseTrimNode(element);
            case "bind":
                return parseBindNode(element);
            case "with":
                return parseWithNode(element);
            case "union":
                return parseUnionNode(element);
            case "join":
                return parseJoinNode(element);
            default:
                throw new SqlParseException("Unknown element: " + tagName);
        }
    }

    private IfNode parseIfNode(Element element) {
        String test = element.getAttribute("test");
        SqlNode body = parseNode(element);
        return new IfNode(test, body, evaluator);
    }

    private ForEachNode parseForEachNode(Element element) {
        String collection = element.getAttribute("collection");
        String item = element.getAttribute("item");
        String index = element.getAttribute("index");
        String open = element.getAttribute("open");
        String close = element.getAttribute("close");
        String separator = element.getAttribute("separator");
        SqlNode body = parseNode(element);
        return new ForEachNode(collection, item, index, open, close, separator, body, evaluator);
    }

    private WhereNode parseWhereNode(Element element) {
        SqlNode body = parseNode(element);
        return new WhereNode(body);
    }

    private SetNode parseSetNode(Element element) {
        SqlNode body = parseNode(element);
        return new SetNode(body);
    }

    private ChooseNode parseChooseNode(Element element) {
        List<WhenNode> whenNodes = new ArrayList<>();
        OtherwiseNode otherwiseNode = null;

        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                String tagName = childElement.getTagName();
                if ("when".equals(tagName)) {
                    whenNodes.add(parseWhenNode(childElement));
                } else if ("otherwise".equals(tagName)) {
                    otherwiseNode = parseOtherwiseNode(childElement);
                }
            }
        }

        return new ChooseNode(whenNodes, otherwiseNode);
    }

    private WhenNode parseWhenNode(Element element) {
        String test = element.getAttribute("test");
        SqlNode body = parseNode(element);
        return new WhenNode(test, body, evaluator);
    }

    private OtherwiseNode parseOtherwiseNode(Element element) {
        SqlNode body = parseNode(element);
        return new OtherwiseNode(body);
    }

    private TrimNode parseTrimNode(Element element) {
        String prefixesToRemove = element.getAttribute("prefixesToRemove");
        String suffixesToRemove = element.getAttribute("suffixesToRemove");
        String prefix = element.getAttribute("prefix");
        String suffix = element.getAttribute("suffix");
        SqlNode body = parseNode(element);
        return new TrimNode(body, prefixesToRemove, suffixesToRemove, prefix, suffix);
    }

    private BindNode parseBindNode(Element element) {
        String name = element.getAttribute("name");
        String expression = element.getAttribute("value");
        return new BindNode(name, expression, evaluator);
    }

    private WithNode parseWithNode(Element element) {
        String alias = element.getAttribute("alias");
        String recursiveAttr = element.getAttribute("recursive");
        boolean recursive = Boolean.parseBoolean(recursiveAttr);
        
        NodeList children = element.getChildNodes();
        SqlNode subquery = null;
        SqlNode body = null;
        
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                String tagName = childElement.getTagName();
                if ("subquery".equals(tagName)) {
                    subquery = parseNode(childElement);
                } else if ("body".equals(tagName)) {
                    body = parseNode(childElement);
                }
            }
        }
        
        return new WithNode(alias, subquery, recursive, body);
    }

    private UnionNode parseUnionNode(Element element) {
        String allAttr = element.getAttribute("all");
        String distinctAttr = element.getAttribute("distinct");
        boolean all = Boolean.parseBoolean(allAttr);
        boolean distinct = Boolean.parseBoolean(distinctAttr);
        
        List<SqlNode> queries = new ArrayList<>();
        NodeList children = element.getChildNodes();
        
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) child;
                if ("query".equals(childElement.getTagName())) {
                    queries.add(parseNode(childElement));
                }
            }
        }
        
        return new UnionNode(queries, all, distinct);
    }

    private JoinNode parseJoinNode(Element element) {
        String typeAttr = element.getAttribute("type");
        String table = element.getAttribute("table");
        String alias = element.getAttribute("alias");
        String onCondition = element.getAttribute("on");
        
        JoinNode.JoinType joinType = JoinNode.JoinType.INNER;
        if (typeAttr != null && !typeAttr.isEmpty()) {
            try {
                joinType = JoinNode.JoinType.valueOf(typeAttr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new SqlParseException("Invalid join type: " + typeAttr);
            }
        }
        
        return new JoinNode(joinType, table, alias, onCondition);
    }
}