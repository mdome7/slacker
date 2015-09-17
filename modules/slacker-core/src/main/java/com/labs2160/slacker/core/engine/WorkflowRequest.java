package com.labs2160.slacker.core.engine;

public class WorkflowRequest {
    private String [] path;
    private String [] args;
    private Workflow workflow;

    public WorkflowRequest(String [] path, String [] args, Workflow wf) {
        this.path = path;
        this.args = args;
        this.workflow = wf;
    }

    public String [] getPath() {
        return path;
    }

    public String [] getArgs() {
        return args;
    }

    public Workflow getWorkflow() {
        return workflow;
    }
}