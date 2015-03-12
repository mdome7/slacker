package com.labs2160.slacker.api;

public class InvalidRequestException extends SlackerException {

	private static final long serialVersionUID = 4142115588838985361L;

	public InvalidRequestException(String msg) {
		super(msg);
	}
	
	public InvalidRequestException(String msg, Throwable t) {
		super(msg, t);
	}
}
