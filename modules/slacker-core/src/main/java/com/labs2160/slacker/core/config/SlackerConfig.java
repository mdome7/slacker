package com.labs2160.slacker.core.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.inject.Singleton;

@Singleton
public class SlackerConfig {

    public final static int DEFAULT_MAX_THREADS = 5;

    public final static String PARAM_CONFIG = "config";

    public final static String PARAM_PLUGIN_DIR = "pluginDir";

    public final static String PARAM_MAX_THREADS = "maxThreads";

    /** max number of threads for the entire application */
    private int maxThreads;

    /** path to config file */
    private Path configFile;

    /** path to plugin directory */
    private Path pluginDir;

    private Properties properties;

    public SlackerConfig() {
        this(new Properties());
    }

    public SlackerConfig(Properties properties) {
        this.properties = properties;
        maxThreads = getIntProperty(PARAM_MAX_THREADS, DEFAULT_MAX_THREADS);
        configFile = getPathProperty(PARAM_CONFIG);
        pluginDir = getPathProperty(PARAM_PLUGIN_DIR);
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public Path getConfigFile() {
        return configFile;
    }

    public void setConfigFile(Path configFile) {
        this.configFile = configFile;
    }

    public Path getPluginDir() {
        return pluginDir;
    }

    public void setPluginDir(Path pluginDir) {
        this.pluginDir = pluginDir;
    }

    private Path getPathProperty(String key) {
        String p = getStringProperty(key);
        if (p != null) {
            return Paths.get(p);
        }
        return null;
    }

    private String getStringProperty(String key) {
        return System.getProperty(key, properties.getProperty(key));
    }

    private Integer getIntProperty(String key) {
        String value = getStringProperty(key);
        return value != null ? Integer.parseInt(value) : null;
    }

    private int getIntProperty(String key, int def) {
        Integer value = getIntProperty(key);
        return value != null ? value : def;
    }
}
