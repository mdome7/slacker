package com.labs2160.slacker.core.engine;

/**
 * Simple metadata describing a workflow.
 */
public class WorkflowMetadata {

	private String [] path;
	
	private String name;
	
	private String argsSpecification;
	
	private String description;
	
	private String exampleArgs;
	
	public WorkflowMetadata(String [] path, String name, String description, String argsSpecification, String exampleArgs) {
		this.path = path;
		this.name = name;
		this.description = description;
		this.argsSpecification = argsSpecification;
		this.exampleArgs = exampleArgs;
	}

	public String [] getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	public String getArgsSpecification() {
		return argsSpecification;
	}

	public String getExampleArgs() {
		return exampleArgs;
	}
}
