package io.sketch.dsql.functional;

import io.sketch.dsql.core.expression.ExpressionFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionFunctionRegistry {

    private final Map<String, Object> functions = new ConcurrentHashMap<>();

    private static volatile ExpressionFunctionRegistry instance;

    private ExpressionFunctionRegistry() {
    }

    public static ExpressionFunctionRegistry getInstance() {
        if (instance == null) {
            synchronized (ExpressionFunctionRegistry.class) {
                if (instance == null) {
                    instance = new ExpressionFunctionRegistry();
                }
            }
        }
        return instance;
    }

    public void register(String name, Object function) {
        functions.put(name, function);
    }

    public void registerAll(Map<String, Object> functions) {
        this.functions.putAll(functions);
    }

    public void registerFromClass(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            ExpressionFunction annotation = method.getAnnotation(ExpressionFunction.class);
            if (annotation != null) {
                String name = annotation.name().isEmpty() ? method.getName() : annotation.name();
                try {
                    final Object instance = clazz.getDeclaredConstructor().newInstance();
                    Class<?>[] interfaces = clazz.getInterfaces();
                    if (interfaces.length > 0) {
                        Object proxy = Proxy.newProxyInstance(
                            clazz.getClassLoader(),
                            interfaces,
                            (proxyObj, proxyMethod, args) -> method.invoke(instance, args)
                        );
                        functions.put(name, proxy);
                    } else {
                        functions.put(name, method);
                    }
                } catch (Exception e) {
                    functions.put(name, method);
                }
            }
        }
    }

    public Object getFunction(String name) {
        return functions.get(name);
    }

    public boolean hasFunction(String name) {
        return functions.containsKey(name);
    }

    public void unregister(String name) {
        functions.remove(name);
    }

    public Map<String, Object> getAllFunctions() {
        return new LinkedHashMap<>(functions);
    }

    public int size() {
        return functions.size();
    }

    public void clear() {
        functions.clear();
    }
}