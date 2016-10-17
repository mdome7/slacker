package com.labs2160.slacker.api;

import java.util.Map;
import java.util.Properties;

public interface WorkflowComponent {
    void setComponents(Map<String, Resource> resources, Properties config) throws InitializationException;
}