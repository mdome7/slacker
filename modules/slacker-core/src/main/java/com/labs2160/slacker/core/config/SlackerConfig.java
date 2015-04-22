package com.labs2160.slacker.core.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class SlackerConfig {

    public final static int DEFAULT_MAX_THREADS = 5;

    /** max number of threads for the entire application */
    private int maxThreads;

    /** path to config file */
    private Path configFile;

    private Properties properties;

    public SlackerConfig() {
        this(new Properties());
    }

    public SlackerConfig(Properties properties) {
        this.properties = properties;
        maxThreads = getIntProperty("maxThreads", DEFAULT_MAX_THREADS);
        String configFilePath = getStringProperty("config");
        if (configFilePath != null) {
            this.configFile = Paths.get(configFilePath);
        }
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
