package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.Action;

public class ActionMetadata {

    private final String name;

    private final String description;

    private final Class<? extends Action>  actionClass;

    private final String argsSpec;

    private final String argsExample;

    public ActionMetadata(Class<? extends Action> actionClass) {
        this(actionClass, null, null, null, null);
    }

    public ActionMetadata(Class<? extends Action> actionClass, String name, String  description, String  argsSpec, String  argsExample) {
        this.actionClass = actionClass;
        this.name = name;
        this.description = description;
        this.argsSpec = argsSpec;
        this.argsExample = argsExample;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }

    public String getActionClassName() {
        return actionClass != null ? actionClass.getName() : null;
    }

    public String getArgsSpec() {
        return argsSpec;
    }

    public String getArgsExample() {
        return argsExample;
    }

    @Override
    public String toString() {
        return "[" + actionClass + "]"
                + " name=" + name
                + ", description=" + description;
    }
}
