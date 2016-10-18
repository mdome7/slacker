package com.labs2160.slacker.api;

/**
 * Keeps the state during processing of the {@code Request}.
 * @author mike
 *
 */
public class SlackerContext {

	private String [] requestPath;

	private String [] requestArgs;

	public SlackerContext(String [] path, String [] requestArgs) {
		if (path == null || path.length == 0) {
			throw new IllegalArgumentException("Workflow path is required.");
		}
		this.requestPath = path;
		this.requestArgs = requestArgs == null ? new String[0] : requestArgs;
	}

	public String [] getRequestPath() {
		return requestPath;
	}

	public String [] getRequestArgs() {
		return requestArgs != null ? requestArgs : new String[0];
	}
}
