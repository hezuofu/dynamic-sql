package io.sketch.dsql.core.node;

import io.sketch.dsql.core.context.DynamicContext;

import java.util.List;
import java.util.Objects;

public class TrimNode implements SqlNode {

    private final SqlNode body;
    private final List<String> prefixesToRemove;
    private final List<String> suffixesToRemove;
    private final String prefix;
    private final String suffix;

    public TrimNode(SqlNode body, String prefixesToRemove, String suffixesToRemove,
                   String prefix, String suffix) {
        this.body = body;
        this.prefixesToRemove = SqlTrimUtils.parsePrefixList(prefixesToRemove);
        this.suffixesToRemove = SqlTrimUtils.parsePrefixList(suffixesToRemove);
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void apply(DynamicContext context) {
        int start = context.getSqlLength();
        body.apply(context);
        String content = context.getSql().substring(start).trim();

        content = SqlTrimUtils.trimPrefixes(content, prefixesToRemove).trim();
        content = SqlTrimUtils.trimSuffixes(content, suffixesToRemove).trim();

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