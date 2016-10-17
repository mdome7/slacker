package com.labs2160.slacker.core.event;

import com.labs2160.slacker.core.engine.WorkflowRequest;

public class WorkflowExecutionEvent {

    private WorkflowExecutionEventType type;

    private String workflowId;

    private WorkflowRequest request;

    private boolean successful;

    private long timestamp;

    public WorkflowExecutionEvent() {}

    public WorkflowExecutionEvent(WorkflowExecutionEventType type, String workflowId, WorkflowRequest request, boolean successful, long tstamp) {
        this.type = type;
        this.workflowId = workflowId;
        this.request = request;
        this.successful = successful;
        this.timestamp = tstamp;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public WorkflowRequest getRequest() {
        return request;
    }

    public void setRequest(WorkflowRequest request) {
        this.request = request;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public WorkflowExecutionEventType getType() {
        return type;
    }

    public void setType(WorkflowExecutionEventType type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setStart(long start) {
        this.timestamp = start;
    }
}
