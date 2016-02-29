package com.labs2160.slacker.api;

import java.util.Map;
import java.util.Properties;

public interface Configurable {
    public void setConfiguration(Map<String, Resource> resources, Properties config);
}