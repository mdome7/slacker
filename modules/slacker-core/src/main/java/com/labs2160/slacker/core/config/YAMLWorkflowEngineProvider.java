package com.labs2160.slacker.core.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.core.engine.Workflow;
import com.labs2160.slacker.core.engine.WorkflowEngine;
import com.labs2160.slacker.core.engine.WorkflowEngineImpl;

/**
 * Initializes the workflow engine from a YAML config file.
 *
 * TODO: cleanup and give better error messages for malformed input
 */
@ApplicationScoped
public class YAMLWorkflowEngineProvider {
    private static final Logger logger = LoggerFactory.getLogger(YAMLWorkflowEngineProvider.class);

    private static final Yaml YAML = new Yaml();

    private SlackerConfig config;

    @Inject
    public YAMLWorkflowEngineProvider(@Named("config") SlackerConfig config) {
        this.config = config;
    }

    @Produces @ApplicationScoped @Named("engine")
    public WorkflowEngine provide() {
        try {
            final long start = System.currentTimeMillis();
            WorkflowEngineImpl engine = new WorkflowEngineImpl();
            Map<String,?> config = getConfig();
            initializeCollectors(engine, parseList(config, "collectors", true));
            initializeWorkflows(engine, parseList(config, "actions", true));
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
                // TODO read default_config.yaml in classpath
                throw new IllegalStateException("Must specify config file!");
            }
        } catch (YAMLException | FileNotFoundException e) {
            throw new InitializationException("Could not parse the config file: " + config.getConfigFile(), e);
        }
        return yamlConfig;
    }

    @SuppressWarnings("unchecked")
    private void initializeCollectors(WorkflowEngineImpl engine, List<?> collectors) {
        for (Object entry : collectors) {
            Map<String,?> collectorEntry = (Map<String,?>) entry;
            final Boolean enabled = parseBoolean(collectorEntry, "enabled", false);
            if (enabled == null || enabled) {
                final String name = parseString(collectorEntry, "name", true);
                final String className = parseString(collectorEntry, "className", true);
                final Properties configuration = parseProperties(collectorEntry, "configuration", false);
                try {
                    logger.info("Initializing collector: {} ({})", name, className);
                    Class<?> clazz = Class.forName(className);
                    if (!RequestCollector.class.isAssignableFrom(clazz)) {
                        throw new InitializationException("Class " + clazz.getName() + " must implement " + RequestCollector.class.getName());
                    }

                    RequestCollector collector = (RequestCollector) clazz.getConstructor().newInstance();
                    collector.setConfiguration(configuration);
                    engine.addCollector(name, collector);
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                    throw new InitializationException("Could not initialize collector \"" + name + "\": " + e.getMessage(), e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void initializeWorkflows(WorkflowEngineImpl engine, List<?> actions) {
        for (Object entry : actions) {
            Map<String,?> actionEntry = (Map<String,?>) entry;
            final String name = parseString(actionEntry, "name", true);
            final String className = parseString(actionEntry, "className", true);
            final String description = parseString(actionEntry, "description", false);
            final String alias = parseString(actionEntry, "alias", true);
            final Properties configuration = parseProperties(actionEntry, "configuration", false);
            try {
                logger.info("Initializing action: {} ({})", name, className);
                Workflow wf = new Workflow(name, description);

                Class<?> clazz = Class.forName(className);
                if (!Action.class.isAssignableFrom(clazz)) {
                    throw new InitializationException("Class " + clazz.getName() + " must implement " + Action.class.getName());
                }
                Action action = (Action) clazz.getConstructor().newInstance();
                action.setConfiguration(configuration);
                wf.addAction(action);
                engine.addWorkflow(wf, alias.split(" "));
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new InitializationException("Could not initialize action \"" + name + "\": " + e.getMessage(), e);
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
        return (List<?>) parseEntry(map, key, required);
    }

    private Object parseEntry(Map<String,?> map, String key, boolean required) {
        Object val = map.get(key);
        if (required && val == null) {
            throw new InitializationException("Missing required parameter \"" + key + "\"");
        }
        return val;
    }
}
