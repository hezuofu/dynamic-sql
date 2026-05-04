package io.sketch.dsql.extension.cache;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

@FunctionalInterface
public interface CacheKeyGenerator {

    String generate(String script, Map<String, Object> parameters);

    static CacheKeyGenerator defaultGenerator() {
        return (script, parameters) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(script);
            if (parameters != null) {
                parameters.keySet().stream().sorted().forEach(key -> {
                    sb.append(key).append("=").append(parameters.get(key));
                });
            }
            return hash(sb.toString());
        };
    }

    static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            return String.valueOf(input.hashCode());
        }
    }
}