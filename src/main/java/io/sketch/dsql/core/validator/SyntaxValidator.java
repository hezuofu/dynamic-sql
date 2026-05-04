package io.sketch.dsql.core.validator;

import java.util.regex.Pattern;

public class SyntaxValidator implements SqlValidator {

    private static final Pattern XML_TAG_PATTERN = Pattern.compile("<([a-zA-Z][a-zA-Z0-9_]*)\\b[^>]*>(.*?)</\\1>");
    private static final Pattern JSON_BRACKET_PATTERN = Pattern.compile("\\{|\\}");
    private static final Pattern SQL_KEYWORDS = Pattern.compile("\\b(SELECT|INSERT|UPDATE|DELETE|FROM|WHERE|AND|OR|NOT|IN|LIKE|BETWEEN|ORDER|GROUP|HAVING|LIMIT|OFFSET|JOIN|LEFT|RIGHT|INNER|OUTER)\\b", Pattern.CASE_INSENSITIVE);

    @Override
    public ValidationResult validate(String script) {
        ValidationResult result = new ValidationResult();

        if (script == null || script.trim().isEmpty()) {
            result.addError("script", "Script cannot be empty");
            return result;
        }

        String trimmed = script.trim();
        
        if (trimmed.startsWith("<")) {
            validateXmlSyntax(trimmed, result);
        } else if (trimmed.startsWith("{")) {
            validateJsonSyntax(trimmed, result);
        } else {
            validateTextSyntax(trimmed, result);
        }

        return result;
    }

    private void validateXmlSyntax(String script, ValidationResult result) {
        int openCount = countOccurrences(script, '<');
        int closeCount = countOccurrences(script, '>');
        
        if (openCount != closeCount) {
            result.addError("xml", "Mismatched XML tags", "XML_TAG_MISMATCH");
        }

        if (!script.contains("</")) {
            result.addError("xml", "Missing closing tags", "XML_MISSING_CLOSE");
        }
    }

    private void validateJsonSyntax(String script, ValidationResult result) {
        int openBrace = countOccurrences(script, '{');
        int closeBrace = countOccurrences(script, '}');
        int openBracket = countOccurrences(script, '[');
        int closeBracket = countOccurrences(script, ']');
        
        if (openBrace != closeBrace) {
            result.addError("json", "Mismatched curly braces", "JSON_BRACE_MISMATCH");
        }
        
        if (openBracket != closeBracket) {
            result.addError("json", "Mismatched square brackets", "JSON_BRACKET_MISMATCH");
        }
    }

    private void validateTextSyntax(String script, ValidationResult result) {
        if (!SQL_KEYWORDS.matcher(script).find()) {
            result.addError("sql", "No SQL keywords found", "SQL_NO_KEYWORDS");
        }
    }

    private int countOccurrences(String str, char c) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}