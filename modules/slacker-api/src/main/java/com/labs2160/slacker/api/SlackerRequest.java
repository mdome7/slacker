package com.labs2160.slacker.api;

/**
 * TODO: Add source info
 * @author mike
 *
 */
public class SlackerRequest {

	private String source;
	
	private String [] rawArguments;

	public SlackerRequest(String source, String ... args) {
		this.source = source;
		this.rawArguments = args;
	}

	public String getSource() {
		return source;
	}

	public String[] getRawArguments() {
		return rawArguments;
	}
}
