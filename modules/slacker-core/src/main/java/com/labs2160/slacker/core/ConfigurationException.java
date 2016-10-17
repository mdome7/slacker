package com.labs2160.slacker.core;

import com.labs2160.slacker.api.SlackerException;

public class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -7946758763792773109L;

    public ConfigurationException(String msg) {
        super(msg);
    }

    public ConfigurationException(String msg, Throwable t) {
        super(msg, t);
    }
}
