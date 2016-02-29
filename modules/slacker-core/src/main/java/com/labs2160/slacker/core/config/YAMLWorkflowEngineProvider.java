package com.labs2160.slacker.core.config;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.core.InitializationException;
import com.labs2160.slacker.core.engine.Workflow;
import com.labs2160.slacker.core.engine.WorkflowEngine;
import com.labs2160.slacker.core.engine.WorkflowEngineImpl;
import com.labs2160.slacker.core.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Initializes the workflow engine from a YAML config file.
 *
 * TODO: cleanup and give better error messages for malformed input
 */
@ApplicationScoped
public class YAMLWorkflowEngineProvider {
    private static final Logger logger = LoggerFactory.getLogger(YAMLWorkflowEngineProvider.class);

    private static final Yaml YAML = new Yaml();

    private final SlackerConfig config;

    private final PluginManager pluginManager;

    private Map<String, Resource> resources;

    @Inject
    public YAMLWorkflowEngineProvider(SlackerConfig config) {
        this.config = config;
        this.pluginManager = new PluginManager(config.getPluginDir());
    }

    @Produces @ApplicationScoped @Named("engine")
    public WorkflowEngine provide() {
        try {
            final long start = System.currentTimeMillis();
            WorkflowEngineImpl engine = new WorkflowEngineImpl();
            Map<String,?> configuration = getConfig();
            resources = provideResources(parseList(configuration, "resources", false));
            initializeCollectors(engine, parseList(configuration, "collectors", true));
            initializeWorkflows(engine, parseList(configuration, "workflows", true));
            initializeTriggers(engine, parseList(configuration, "triggers", false));
            logger.debug("Engine initialized in {} ms", System.currentTimeMillis() - start);
            return engine;
        } catch (InitializationException e) {
            throw e;
        } catch (Exception e) {
            throw new InitializationException("Error while initializing engine: " + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String,?> getConfig() {
        Map<String,?> yamlConfig = null;
        try {
            if (config.getConfigFile() != null) {
                logger.info("Loading configuration: {}", config.getConfigFile());
                yamlConfig = (Map<String, ?>) YAML.load(new FileReader(config.getConfigFile().toFile()));
            } else {
                throw new IllegalStateException("Must specify config file!");
            }
        } catch (YAMLException | FileNotFoundException e) {
            throw new InitializationException("Could not parse the config file: " + config.getConfigFile(), e);
        }
        return yamlConfig;
    }

    public Map<String, Resource> provideResources(List<?> resourceList) {
        Map<String, Resource> resources = new ConcurrentHashMap<>();
        for (Object entry : resourceList) {
            Map<String,?> resourceEntry = (Map<String,?>) entry;
            final String name = parseString(resourceEntry, "name", true);
            final String plugin = parseString(resourceEntry, "plugin", false);
            final String className = parseString(resourceEntry, "className", true);
            final Properties configuration = parseProperties(resourceEntry, "configuration", true);
            try {
                Resource resource = pluginManager.getResourceInstance(plugin, className);
                resource.setConfiguration(configuration);
                resources.put(name, resource);
            } catch ( IllegalArgumentException | SecurityException e) {
                throw new InitializationException("Could not initialize resource \"" + name + "\": " + e.getMessage(), e);
            }
        }
        return resources;
    }

    @SuppressWarnings("unchecked")
    private void initializeCollectors(WorkflowEngineImpl engine, List<?> collectors) {
        for (Object entry : collectors) {
            Map<String,?> collectorEntry = (Map<String,?>) entry;
            final Boolean enabled = parseBoolean(collectorEntry, "enabled", false);
            if (enabled == null || enabled) {
                final String name = parseString(collectorEntry, "name", true);
                final String plugin = parseString(collectorEntry, "plugin", false);
                final String className = parseString(collectorEntry, "className", true);
                final Properties configuration = parseProperties(collectorEntry, "configuration", false);
                try {
                    RequestCollector collector = pluginManager.getRequestCollectorInstance(plugin, className);
                    collector.setConfiguration(resources, configuration);
                    engine.addCollector(name, collector);
                } catch ( IllegalArgumentException | SecurityException e) {
                    throw new InitializationException("Could not initialize collector \"" + name + "\": " + e.getMessage(), e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeWorkflows(WorkflowEngineImpl engine, List<?> workflows) {
        for (Object entry : workflows) {
            Map<String,?> workflowEntry = (Map<String,?>) entry;
            final String name = parseString(workflowEntry, "name", true);
            final String alias = parseString(workflowEntry, "alias", true);
            final String description = parseString(workflowEntry, "description", false);

            final Map<String,?> actionEntry = (Map<String,?>) parseEntry(workflowEntry, "action", true);
            final List<?> endpointEntries = parseList(workflowEntry, "endpoints", false);
            try {
                logger.info("Initializing workflow: {}", name);
                Workflow wf = new Workflow(name, description);

                wf.addAction(parseAction(actionEntry));
                for (Endpoint endpoint : parseEndpoints(endpointEntries)) {
                    wf.addEndpoint(endpoint);
                }

                engine.addWorkflow(wf, alias.split(" "));
            } catch (SecurityException | IllegalArgumentException e) {
                throw new InitializationException("Could not initialize workflow \"" + name + "\": " + e.getMessage(), e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Endpoint> parseEndpoints(List<?> endpointList) {
        List<Endpoint> endpoints = new ArrayList<>(endpointList.size());
        for (Object endpoint: endpointList) {
            endpoints.add(parseEndpoint((Map<String,?>) endpoint));
        }
        return endpoints;
    }

    public Action parseAction(Map<String,?> entry) {
        final String plugin = parseString(entry, "plugin", false);
        final String className = parseString(entry, "className", true);
        final Properties configuration = parseProperties(entry, "configuration", false);
        Action action = pluginManager.getActionInstance(plugin, className);
        action.setConfiguration(resources, configuration);
        return action;
    }

    public Endpoint parseEndpoint(Map<String,?> entry) {
        final String plugin = parseString(entry, "plugin", false);
        final String className = parseString(entry, "className", true);
        final Properties configuration = parseProperties(entry, "configuration", false);
        Endpoint endpoint = pluginManager.getEndpointInstance(plugin, className);
        endpoint.setConfiguration(resources, configuration);
        return endpoint;
    }

    /**
     * TODO: THIS NEEDS TO BE REDONE
     * @param engine
     * @param triggers
     */
    @SuppressWarnings("unchecked")
    private void initializeTriggers(WorkflowEngineImpl engine, List<?> triggers) {
        if (triggers == null) {
            // No triggers in config
            return;
        }
        for (Object entry : triggers) {
            Map<String,?> triggerEntry = (Map<String,?>) entry;
            final String name = parseString(triggerEntry, "name", true);
            final String triggerClass = parseString(triggerEntry, "triggerClass", true);
            final Properties configuration = parseProperties(triggerEntry, "configuration", false);
            try {
                logger.info("Initializing trigger: {}", name);
                Class<?> clazz = Class.forName(triggerClass);
                if (!Trigger.class.isAssignableFrom(clazz)) {
                    throw new InitializationException("Class " + clazz.getName() + " must implement " + Trigger.class.getName());
                }
                Trigger trigger = (Trigger) clazz.getConstructor().newInstance();
                trigger.setConfiguration(resources, configuration);
                engine.addTrigger(name, trigger);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new InitializationException("Could not initialize trigger \"" + name + "\": " + e.getMessage(), e);
            }
        }
    }

    private Boolean parseBoolean(Map<String,?> map, String key, boolean required) {
        return (Boolean) parseEntry(map, key, required);
    }

    private String parseString(Map<String,?> map, String key, boolean required) {
        String value = (String) parseEntry(map, key, required);
        if (required && value.trim().isEmpty()) {
            throw new InitializationException("Missing required parameter \"" + key + "\"");
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private Properties parseProperties(Map<String,?> map, String key, boolean required) {
        Properties config = new Properties();
        Map<String,?> configMap = (Map<String,?>) parseEntry(map, key, required);
        if (configMap != null) {
            for (String configKey : configMap.keySet()) {
                config.setProperty(configKey, configMap.get(configKey).toString());
            }
        }
        return config;
    }

    private List<?> parseList(Map<String,?> map, String key, boolean required) {
        List<?> list = (List<?>) parseEntry(map, key, required);
        return list == null ? new ArrayList<>(0) : list;
    }

    private Object parseEntry(Map<String,?> map, String key, boolean required) {
        Object val = map.get(key);
        if (required && val == null) {
            throw new InitializationException("Missing required parameter \"" + key + "\"");
        }
        return val;
    }
}
