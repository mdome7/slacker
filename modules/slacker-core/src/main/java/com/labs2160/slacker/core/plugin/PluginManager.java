package com.labs2160.slacker.core.plugin;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.core.ConfigurationException;
import com.labs2160.slacker.core.lib.DirClassLoaderRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Set;

public class PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(PluginManager.class);

    private DirClassLoaderRegistry clRegistry;

    public PluginManager(Path pluginDirectory) {
        clRegistry = new DirClassLoaderRegistry(pluginDirectory);
    }

    public Path getPluginDirectory() {
        return clRegistry.getBaseDirectory();
    }

    public Set<String> listPlugins() {
        return clRegistry.getClassLoaderDirNames();
    }

    public Resource getResourceInstance(String pluginName, String className) {
        return getPluginComponentInstance(pluginName, className, Resource.class);
    }

    public RequestCollector getRequestCollectorInstance(String pluginName, String className) {
        return getPluginComponentInstance(pluginName, className, RequestCollector.class);
    }

    public Action getActionInstance(String pluginName, String className) {
        return getPluginComponentInstance(pluginName, className, Action.class);
    }

    public Endpoint getEndpointInstance(String pluginName, String className) {
        return getPluginComponentInstance(pluginName, className, Endpoint.class);
    }

    @SuppressWarnings("unchecked")
    private <C> C getPluginComponentInstance(String pluginName, String className, Class<C> componentClass) {
        logger.debug("Creating {} instance for plugin={}, class={}", componentClass.getSimpleName(), pluginName, className);
        ClassLoader pluginCl = null;
        if (pluginName == null) {
            pluginCl = PluginManager.class.getClassLoader();
        } else {
            pluginCl = clRegistry.getClassLoader(pluginName);
            if (pluginCl == null) {
                throw new ConfigurationException("Cannot find plugin \"" + pluginName + "\""); // TODO throw better exception
            }
        }
        try {
            Object action = pluginCl.loadClass(className).newInstance();
            if (componentClass.isAssignableFrom(action.getClass())) {
                return (C) action;
            } else if (componentClass != pluginCl.loadClass(componentClass.getName())) {
                throw new InitializationException("Cannot instantiate action " + className + " - plugin directory contains a duplicate " + Action.class.getName());
            } else {
                throw new InitializationException("Cannot instantiate action - class " + className + " does not extend " + Action.class.getName());
            }
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InitializationException e) {
            throw new ConfigurationException("Cannot instantiate action (" + pluginName + ") " + className, e);
        }
    }
}
