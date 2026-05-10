package io.sketch.dsql.builder;

import io.sketch.dsql.builder.meta.Column;
import io.sketch.dsql.builder.meta.Table;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

final class EntityResolver {

    private static final Map<Class<?>, Object> cache = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Map<String, Field<?, ?>>> fields = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    static <T> T create(Class<T> type) { return (T) cache.computeIfAbsent(type, EntityResolver::build); }

    static Field<?, ?> field(Class<?> type, Method m) {
        return fields.computeIfAbsent(type, EntityResolver::scan).get(m.getName());
    }

    static String tableName(Class<?> type) {
        Table t = type.getAnnotation(Table.class);
        if (t == null) throw new SqlBuilderException(type + " missing @Table");
        return t.value();
    }

    private static Map<String, Field<?, ?>> scan(Class<?> type) {
        return Arrays.stream(type.getMethods())
                .filter(m -> m.getAnnotation(Column.class) != null)
                .collect(Collectors.toMap(Method::getName, m -> {
                    Column c = m.getAnnotation(Column.class);
                    return new Field<>(c.value().isEmpty() ? m.getName() : c.value(), m.getReturnType());
                }));
    }

    @SuppressWarnings("unchecked")
    private static <T> T build(Class<T> type) {
        tableName(type); // validate
        Map<String, Field<?, ?>> fds = fields.computeIfAbsent(type, EntityResolver::scan);
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type, GroupTable.class},
                (proxy, method, args) -> {
                    if (method.getDeclaringClass() == GroupTable.class && "star".equals(method.getName()))
                        return Field.of("*", Long.class);
                    Field<?, ?> f = fds.get(method.getName());
                    if (f != null) return f;
                    return switch (method.getName()) {
                        case "toString" -> "Proxy(" + type.getSimpleName() + ")";
                        case "hashCode" -> System.identityHashCode(proxy);
                        case "equals" -> proxy == args[0];
                        default -> throw new SqlBuilderException("Unknown: " + method.getName());
                    };
                });
    }
}
