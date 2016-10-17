package com.labs2160.slacker.core.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRegistry {

    private final static Logger logger = LoggerFactory.getLogger(WorkflowRegistry.class);

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

    public RegistryNode findWorkflowMatch(final String ... path) {
        RegistryNode match = null;
        RegistryNode node = root;
        RegistryNode candidate = null;
        for (int i = 0; i < path.length; i++) {
            candidate = node.getChild(path[i].replaceAll("\\s+", ""));
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

    public Workflow findWorkflow(final String ... path) {
        final RegistryNode node = findWorkflowMatch(path);
        return node == null ? null : node.getWorkflow();
    }

    private void getMetadataHelper(RegistryNode node, List<WorkflowMetadata> metadata) {
        final Workflow wf = node.getWorkflow();
        if (wf != null) {
            // TODO: finalize workflow theory
            // Currently, a workflow is synonymous to a single action but could potentially
            // be a true workflow with multiple actions, endpoints and control logic.
            // So for now, use action metadata as defaults
            ActionMetadata am = ActionMetadataExtractor.extract(wf.getActions().get(0).getClass());
            metadata.add(new WorkflowMetadata(node.getPath(),
                    wf.getName() != null ? wf.getName() : am.getName(),
                    wf.getDescription() != null ? wf.getDescription() : am.getDescription(),
                    wf.getArgsSpecification() != null ? wf.getArgsSpecification() : am.getArgsSpec(),
                    wf.getExampleArgs() != null ? wf.getExampleArgs() : am.getArgsExample()));
        }
        for (RegistryNode child : node.getChildren()) {
            getMetadataHelper(child, metadata);
        }
    }
}
