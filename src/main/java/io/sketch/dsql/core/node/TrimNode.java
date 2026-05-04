package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TrimNode implements SqlNode {

    private final SqlNode body;
    private final Set<String> prefixesToRemove;
    private final Set<String> suffixesToRemove;
    private final String prefix;
    private final String suffix;

    public TrimNode(SqlNode body, String prefixesToRemove, String suffixesToRemove, 
                   String prefix, String suffix) {
        this.body = body;
        this.prefixesToRemove = parseStringSet(prefixesToRemove);
        this.suffixesToRemove = parseStringSet(suffixesToRemove);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    private Set<String> parseStringSet(String str) {
        if (str == null || str.isEmpty()) {
            return Set.of();
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    @Override
    public void apply(DynamicContext context) {
        int start = context.getSqlLength();
        body.apply(context);
        String content = context.getSql().substring(start);
        
        content = content.trim();
        content = trimPrefixes(content);
        content = trimSuffixes(content);
        
        if (!content.isEmpty()) {
            context.setSqlLength(start);
            if (prefix != null && !prefix.isEmpty()) {
                context.appendSql(prefix);
            }
            context.appendSql(content);
            if (suffix != null && !suffix.isEmpty()) {
                context.appendSql(suffix);
            }
        } else {
            context.setSqlLength(start);
        }
    }

    private String trimPrefixes(String str) {
        String result = str;
        for (String prefix : prefixesToRemove) {
            while (result.startsWith(prefix)) {
                result = result.substring(prefix.length()).trim();
            }
        }
        return result;
    }

    private String trimSuffixes(String str) {
        String result = str;
        for (String suffix : suffixesToRemove) {
            while (result.endsWith(suffix)) {
                result = result.substring(0, result.length() - suffix.length()).trim();
            }
        }
        return result;
    }

    public SqlNode getBody() {
        return body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrimNode trimNode = (TrimNode) o;
        return Objects.equals(body, trimNode.body) &&
                Objects.equals(prefixesToRemove, trimNode.prefixesToRemove) &&
                Objects.equals(suffixesToRemove, trimNode.suffixesToRemove) &&
                Objects.equals(prefix, trimNode.prefix) &&
                Objects.equals(suffix, trimNode.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(body, prefixesToRemove, suffixesToRemove, prefix, suffix);
    }

    @Override
    public String toString() {
        return "TrimNode{" +
                "prefixesToRemove=" + prefixesToRemove +
                ", suffixesToRemove=" + suffixesToRemove +
                ", prefix='" + prefix + '\'' +
                ", suffix='" + suffix + '\'' +
                ", body=" + body +
                '}';
    }
}