package com.labs2160.slacker.api;

/**
 * TODO: Add source info
 * @author mike
 *
 */
public class Request {

	private String source;
	
	private String [] args;

	public Request(String source, String ... args) {
		this.source = source;
		this.args = args;
	}

	public String getSource() {
		return source;
	}

	public String[] getArgs() {
		return args;
	}
}
