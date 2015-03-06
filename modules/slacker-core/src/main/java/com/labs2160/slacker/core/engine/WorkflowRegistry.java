package com.labs2160.slacker.core.engine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WorkflowRegistry {
	
	private RegistryNode root;
	
	private class RegistryNode {
		private RegistryNode parent;
		
		private Map<String,RegistryNode> children;
		
		private String pathPart;
		private Workflow wf;

		public RegistryNode(String pathPart) {
			this.pathPart = pathPart;
			this.wf = null;
			this.parent = null;
			Map<String,RegistryNode> children = new HashMap<>();
		}
		
		public void addChild(RegistryNode child) {
			this.children.put(child.pathPart, child);
		}
	}
	
	public void addWorkFlow(Workflow wf, String ... path) {
		this.root = addWorkFlowHelper(this.root, wf, path);
	}

	private RegistryNode addWorkFlowHelper(RegistryNode node, Workflow wf, String ... path) {
		
		if (node == null) {
			node = new RegistryNode(path[0]);
		}
		
		if (path.length == 1) {
			node.wf = wf;
		} else {
			//node.children addWorkFlowHelper(node, wf, Arrays.copyOfRange(path, 1, path.length));
		}
		
		return node;
	}
}
