package com.labs2160.slacker.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps the state of the workflow during execution.
 * @author mike
 *
 */
public class WorkflowContext {
	
	private String workflowKey;

	private String [] workflowArgs;
	
	private String outputMessage;

	private Map<String,Object> values;
	
	public WorkflowContext(String key, String ... requestArgs) {
		if (key == null || key.length() == 0) {
			throw new IllegalArgumentException("Workflow key is required.");
		}
		if (requestArgs == null || requestArgs.length < 1) {
			throw new IllegalArgumentException("Workflow arguments are required.");
		}
		this.workflowArgs = requestArgs;
		this.values = new ConcurrentHashMap<>();
	}
	
	public String getWorkflowKey() {
		return workflowKey;
	}

	public String [] getWorkflowArgs() {
		return workflowArgs;
	}

	public String getOutputMessage() {
		return outputMessage;
	}

	public void setOutputMessage(String outputMessage) {
		this.outputMessage = outputMessage;
	}
	
	public void addValue(String key, Object value) {
		values.put(key, value);
	}
	
	public Object getValue(String key) {
		return values.get(key);
	}
}
