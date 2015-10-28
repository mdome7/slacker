package com.labs2160.slacker.core.plugin;

import java.util.List;

/**
 * Metadata describing the plugin.  Lists
 * the available components that are packaged
 * with the plugin.
 *
 * @author mdometita
 *
 */
public class PluginMetadata {

    /** plugin name */
    private String name;

    private List<PluginComponent> collectors;

    private List<PluginComponent> actions;

    private List<PluginComponent> endpoints;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PluginComponent> getCollectors() {
        return collectors;
    }

    public void setCollectors(List<PluginComponent> collectors) {
        this.collectors = collectors;
    }

    public List<PluginComponent> getActions() {
        return actions;
    }

    public void setActions(List<PluginComponent> actions) {
        this.actions = actions;
    }

    public List<PluginComponent> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<PluginComponent> endpoints) {
        this.endpoints = endpoints;
    }
}
