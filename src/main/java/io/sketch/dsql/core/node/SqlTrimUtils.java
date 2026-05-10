package io.sketch.dsql.core.node;

import java.util.Arrays;
import java.util.List;

final class SqlTrimUtils {

    private SqlTrimUtils() {
    }

    static String trimPrefixes(String str, List<String> prefixes) {
        String result = str;
        boolean changed;
        do {
            changed = false;
            for (String prefix : prefixes) {
                if (result.startsWith(prefix)) {
                    result = result.substring(prefix.length());
                    changed = true;
                    break;
                }
            }
        } while (changed);
        return result;
    }

    static String trimSuffixes(String str, List<String> suffixes) {
        String result = str;
        boolean changed;
        do {
            changed = false;
            for (String suffix : suffixes) {
                if (result.endsWith(suffix)) {
                    result = result.substring(0, result.length() - suffix.length());
                    changed = true;
                    break;
                }
            }
        } while (changed);
        return result;
    }

    static List<String> parsePrefixList(String str) {
        if (str == null || str.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(str.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
