package com.labs2160.slacker.core.engine;

public class InvalidRequestException extends Exception {

	private static final long serialVersionUID = 4142115588838985361L;

	public InvalidRequestException(String msg) {
		super(msg);
	}
	
	public InvalidRequestException(String msg, Throwable t) {
		super(msg, t);
	}
}
