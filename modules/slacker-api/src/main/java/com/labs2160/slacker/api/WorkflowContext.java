package com.labs2160.slacker.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the state of the workflow during execution.
 * @author mike
 *
 */
public class WorkflowContext {
	
	private String [] requestPath;

	private String [] requestArgs;
	
	private String responseMessage;

	private Map<String,Object> values;
	
	public WorkflowContext(String [] path, String [] requestArgs) {
		if (path == null || path.length == 0) {
			throw new IllegalArgumentException("Workflow path is required.");
		}
		
		this.requestArgs = requestArgs == null ? new String[0] : requestArgs;
		this.values = new ConcurrentHashMap<>();
	}
	
	public String [] getRequestPath() {
		return requestPath;
	}

	public String [] getRequestArgs() {
		return requestArgs != null ? requestArgs : new String[0];
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String outputMessage) {
		this.responseMessage = outputMessage;
	}
	
	public void addValue(String key, Object value) {
		values.put(key, value);
	}
	
	public Object getValue(String key) {
		return values.get(key);
	}
}
