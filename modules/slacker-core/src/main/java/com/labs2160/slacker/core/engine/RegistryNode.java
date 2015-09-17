package com.labs2160.slacker.core.engine;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RegistryNode {
	private Map<String,RegistryNode> children;
	private String [] path;
	private Workflow workflow;

	public RegistryNode(String ... path) {
		this(null, path);
	}

	public RegistryNode(Workflow wf, String ... path) {
		this.path = path;
		this.workflow = wf;
		this.children = new HashMap<>();
	}
	public String [] getPath() {
		return path;
	}

	public void setWorkflow(Workflow workflow) {
		this.workflow = workflow;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public RegistryNode getOrCreateChild(String pathPart) {
		RegistryNode child = children.get(pathPart);
		if (child != null) {
			return child;
		} else {
			String [] childPath = Arrays.copyOfRange(path, 0, path.length + 1);
			childPath[path.length] = pathPart;
			child = new RegistryNode(childPath);
			children.put(pathPart, child);
		}
		return child;
	}

	public RegistryNode getChild(String pathPart) {
		return children.get(pathPart);
	}

	public Collection<RegistryNode> getChildren() {
		return children.values();
	}
}