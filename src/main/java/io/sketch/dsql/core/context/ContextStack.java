package io.sketch.dsql.core.context;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ContextStack {

    private final Deque<Map<String, ContextVariable>> stack = new ArrayDeque<>();

    public void push() {
        stack.push(new LinkedHashMap<>());
    }

    public void pop() {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Context stack is empty");
        }
        stack.pop();
    }

    public void setVariable(String name, Object value) {
        setVariable(name, value, true);
    }

    public void setVariable(String name, Object value, boolean modifiable) {
        if (stack.isEmpty()) {
            push();
        }
        stack.peek().put(name, ContextVariable.builder()
                .name(name)
                .value(value)
                .modifiable(modifiable)
                .build());
    }

    public Optional<Object> getVariable(String name) {
        for (Map<String, ContextVariable> frame : stack) {
            ContextVariable variable = frame.get(name);
            if (variable != null) {
                return Optional.of(variable.getValue());
            }
        }
        return Optional.empty();
    }

    public boolean containsVariable(String name) {
        for (Map<String, ContextVariable> frame : stack) {
            if (frame.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public void removeVariable(String name) {
        if (!stack.isEmpty()) {
            stack.peek().remove(name);
        }
    }

    public int getDepth() {
        return stack.size();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public Map<String, Object> getAllVariables() {
        Map<String, Object> variables = new LinkedHashMap<>();
        stack.descendingIterator().forEachRemaining(frame -> {
            frame.forEach((name, variable) -> {
                variables.putIfAbsent(name, variable.getValue());
            });
        });
        return variables;
    }

    public void clear() {
        stack.clear();
    }

    ContextStack snapshot() {
        ContextStack copy = new ContextStack();
        for (Map<String, ContextVariable> frame : stack) {
            Map<String, ContextVariable> frameCopy = new LinkedHashMap<>();
            for (Map.Entry<String, ContextVariable> entry : frame.entrySet()) {
                ContextVariable var = entry.getValue();
                frameCopy.put(entry.getKey(), ContextVariable.builder()
                        .name(var.getName())
                        .value(var.getValue())
                        .modifiable(var.isModifiable())
                        .build());
            }
            copy.stack.push(frameCopy);
        }
        return copy;
    }

    Iterable<Map<String, ContextVariable>> getFrames() {
        return () -> stack.descendingIterator();
    }
}