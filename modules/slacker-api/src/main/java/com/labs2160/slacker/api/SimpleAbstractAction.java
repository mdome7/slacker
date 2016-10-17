package com.labs2160.slacker.api;

import com.labs2160.slacker.api.*;

import java.util.Map;
import java.util.Properties;

/**
 * Utility base action that has stub/empty implementations of non-essential methods.
 * Child classes need only implement execute method.
 */
public abstract class SimpleAbstractAction implements Action {
    @Override
    public void setComponents(Map<String, Resource> resources, Properties config) throws InitializationException {
        // Do nothing
    }
}
