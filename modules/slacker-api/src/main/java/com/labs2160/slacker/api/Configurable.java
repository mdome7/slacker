package com.labs2160.slacker.api;

import java.util.Properties;

public interface Configurable {
    void setConfiguration(Properties config) throws InitializationException;
}