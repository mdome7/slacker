package com.labs2160.slacker.core.plugin;

/**
 * Simple value object describing a plugin component
 * such as an Action, Collector, or Endpoint.
 * @author mdometita
 *
 */
public class PluginComponent {

    private String name;

    private String className;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
