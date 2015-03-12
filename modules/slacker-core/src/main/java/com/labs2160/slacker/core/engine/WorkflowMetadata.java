package com.labs2160.slacker.core.engine;

/**
 * Simple metadata describing a workflow.
 */
public class WorkflowMetadata {

	private String [] path;
	
	private String name;
	
	private String description;
	
	private String exampleArgs;
	
	public WorkflowMetadata(String [] path, String name, String description, String exampleArgs) {
		this.path = path;
		this.name = name;
		this.description = description;
		this.exampleArgs = exampleArgs;
	}

	public String [] getPath() {
		return path;
	}

	public void setPath(String [] path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExampleArgs() {
		return exampleArgs;
	}

	public void setExampleArgs(String exampleArgs) {
		this.exampleArgs = exampleArgs;
	}
}
