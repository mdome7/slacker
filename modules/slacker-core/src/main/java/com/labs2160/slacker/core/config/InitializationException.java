package com.labs2160.slacker.core.config;

public class InitializationException extends RuntimeException {

    private static final long serialVersionUID = -7946758763792773109L;

    public InitializationException(String msg) {
        super(msg);
    }

    public InitializationException(String msg, Throwable t) {
        super(msg, t);
    }
}
