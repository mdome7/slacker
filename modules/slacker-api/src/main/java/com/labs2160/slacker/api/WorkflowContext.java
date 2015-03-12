package com.labs2160.slacker.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the state of the workflow during execution.
 * @author mike
 *
 */
public class WorkflowContext {
	
	private String [] workflowKey;

	private String [] workflowArgs;
	
	private String responseMessage;

	private Map<String,Object> values;
	
	public WorkflowContext(String [] key, String [] requestArgs) {
		if (key == null || key.length == 0) {
			throw new IllegalArgumentException("Workflow path is required.");
		}
		
		this.workflowArgs = requestArgs == null ? new String[0] : requestArgs;
		this.values = new ConcurrentHashMap<>();
	}
	
	public String [] getWorkflowKey() {
		return workflowKey;
	}

	public String [] getWorkflowArgs() {
		return workflowArgs;
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
