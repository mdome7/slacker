package com.labs2160.slacker.api;

/**
 * TODO: Add source info
 * @author mike
 *
 */
public class Request {

	private String key;
	
	private String [] args;
	
	public Request(String key, String ... args) {
		this.key = key;
		this.args = args;
	}

	public String getKey() {
		return key;
	}

	public String[] getArgs() {
		return args;
	}
}
