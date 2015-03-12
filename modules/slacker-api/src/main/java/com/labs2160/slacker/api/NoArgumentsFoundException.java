package com.labs2160.slacker.api;

/**
 * Use to indicate if arguments are required but not supplied.
 */
public class NoArgumentsFoundException extends SlackerException {

	private static final long serialVersionUID = 4142115588838985361L;

	public NoArgumentsFoundException(String msg) {
		super(msg);
	}
	
	public NoArgumentsFoundException(String msg, Throwable t) {
		super(msg, t);
	}
}
