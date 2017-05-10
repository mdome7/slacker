package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.response.SlackerOutput;

/**
 * Tracks simple metadata around a workflow execution.
 */
public class ExecutionDetails {

    private final String workflowName;

    private final long startTimestamp;

    private final long endTimestamp;

    private final SlackerOutput output;

    public ExecutionDetails(String workflowName, long startTimestamp, long endTimestamp, SlackerOutput output) {
        this.workflowName = workflowName;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.output = output;
    }

    public String getWorkflowName() {
        return workflowName;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public SlackerOutput getOutput() {
        return output;
    }
}
