package com.labs2160.slacker.api;

/**
 * Encapsulates a request.
 */
public class SlackerRequest {

	/** component (i.e. Collector, Trigger) where the request came from */
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
