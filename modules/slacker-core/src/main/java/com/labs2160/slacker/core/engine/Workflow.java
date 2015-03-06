package com.labs2160.slacker.core.engine;

import java.util.ArrayList;
import java.util.List;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.Endpoint;

/**
 * Keep it simple.
 * We don't need to support a complex engine (for now).
 */
public class Workflow {
	private String name;

	private String description;
	
	private String exampleArgs;

	private List<Action> actions;
	
	private List<Endpoint> endpoints;
	
	public Workflow(String name, String description) {
		this.name = name;
		this.description = description;
		this.actions = new ArrayList<>();
		this.endpoints = new ArrayList<>();
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
	
	public void addAction(Action a) {
		actions.add(a);
	}
	
	public void addEndpoint(Endpoint e) {
		endpoints.add(e);
	}

	public List<Action> getActions() {
		return actions;
	}

	public void setActions(List<Action> actions) {
		this.actions = actions;
	}

	public List<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}
}
