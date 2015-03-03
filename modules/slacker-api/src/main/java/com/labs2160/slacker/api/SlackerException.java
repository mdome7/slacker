package com.labs2160.slacker.api;

public class SlackerException extends Exception {

	private static final long serialVersionUID = -7946758763792773109L;

	public SlackerException(String msg) {
		super(msg);
	}
	
	public SlackerException(String msg, Throwable t) {
		super(msg, t);
	}
}
