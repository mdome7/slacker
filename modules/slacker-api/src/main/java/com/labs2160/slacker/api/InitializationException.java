package com.labs2160.slacker.api;

public class InitializationException extends SlackerException {

    private static final long serialVersionUID = -7946758763792773109L;

    public InitializationException(String msg) {
        super(msg);
    }

    public InitializationException(String msg, Throwable t) {
        super(msg, t);
    }
}
