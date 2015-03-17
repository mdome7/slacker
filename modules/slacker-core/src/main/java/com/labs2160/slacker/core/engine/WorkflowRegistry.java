package com.labs2160.slacker.core.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorkflowRegistry {
	
	private RegistryNode root;
	
	public WorkflowRegistry() {
		this.root = new RegistryNode();
	}
	
	/**
	 * Add a workflow to the registry with the given path.
	 * @param wf
	 * @param path
	 */
	public void addWorkflow(Workflow wf, String ... path) {
		if (wf == null || path == null) {
			throw new IllegalArgumentException("Workflow and path are required");
		}
		this.root = addWorkFlowHelper(this.root, wf, path);
	}

	private RegistryNode addWorkFlowHelper(RegistryNode node, Workflow wf, String ... path) {
		final String pathPart = path[0];
		RegistryNode child = node.getOrCreateChild(pathPart);
		if (path.length == 1) {
			child.setWorkflow(wf);
		} else {
			addWorkFlowHelper(child, wf, Arrays.copyOfRange(path, 1, path.length));
		}
		
		return node;
	}
	
	public RegistryNode findWorkflowMatch(String ... path) {
		RegistryNode match = null;
		RegistryNode node = root;
		RegistryNode candidate = null;
		for (int i = 0; i < path.length; i++) {
			candidate = node.getChild(path[i]);
			if (candidate == null) {
				break;
			} else if (candidate.getWorkflow() != null) {
				match = candidate;
			}
			node = candidate;
		}
		return match;
	}

	public List<WorkflowMetadata> getWorkflowMetadata() {
		List<WorkflowMetadata> metadata = new ArrayList<>();
		getMetadataHelper(root, metadata);
		return metadata;
	}
	
	void getMetadataHelper(RegistryNode node, List<WorkflowMetadata> metadata) {
		final Workflow wf = node.getWorkflow();
		if (wf != null) {
			metadata.add(new WorkflowMetadata(node.getPath(),
					wf.getName(),
					wf.getDescription(),
					wf.getArgsSpecification(),
					wf.getExampleArgs()));
		}
		for (RegistryNode child : node.getChildren()) {
			getMetadataHelper(child, metadata);
		}
	}
}
