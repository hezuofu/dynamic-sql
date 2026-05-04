package io.sketch.dsql.extension.plugin;

import java.util.ArrayList;
import java.util.List;

public class PluginChain {

    private final List<Plugin> plugins = new ArrayList<>();
    private int index = -1;

    public PluginChain addPlugin(Plugin plugin) {
        plugins.add(plugin);
        return this;
    }

    public PluginChain addPlugins(List<Plugin> plugins) {
        this.plugins.addAll(plugins);
        return this;
    }

    public void doInit() {
        for (Plugin plugin : plugins) {
            plugin.init();
        }
    }

    public void doDestroy() {
        for (int i = plugins.size() - 1; i >= 0; i--) {
            plugins.get(i).destroy();
        }
    }

    public Plugin getPlugin(String name) {
        return plugins.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }

    public List<Plugin> getPlugins() {
        return new ArrayList<>(plugins);
    }

    public int size() {
        return plugins.size();
    }

    public void clear() {
        plugins.clear();
    }
}