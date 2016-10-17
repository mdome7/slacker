package com.labs2160.slacker.api;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the state during processing of the {@code Request}.
 * @author mike
 *
 */
public class SlackerContext {

	private String [] requestPath;

	private String [] requestArgs;

	private SlackerResponse response;

	private Map<String,Object> values;

	public SlackerContext(String [] path, String [] requestArgs) {
		if (path == null || path.length == 0) {
			throw new IllegalArgumentException("Workflow path is required.");
		}

		this.requestArgs = requestArgs == null ? new String[0] : requestArgs;
		this.values = new ConcurrentHashMap<>();
		this.response = new SlackerResponse();
	}

	public String [] getRequestPath() {
		return requestPath;
	}

	public String [] getRequestArgs() {
		return requestArgs != null ? requestArgs : new String[0];
	}

	public void setResponseMessage(String message) {
		response.setMessage(message);
	}

	public void setAttachedMedia(InputStream is, String mediaType) {
		response.setAttachedMedia(is);
		response.setAttachedMediaType(mediaType);
	}

	public SlackerResponse getResponse() {
		return response;
	}

	public void addValue(String key, Object value) {
		values.put(key, value);
	}

	public Object getValue(String key) {
		return values.get(key);
	}
}
