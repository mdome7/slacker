package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.response.SlackerOutput;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a scheduled task and keeps track of basic stats around its executions.
 */
public class SchedulerTask {

    private final String name;

    private final String schedule;

    private final String workflowAlias;

    private final AtomicInteger executionCount; // audit trail

    private ExecutionDetails lastExecutionDetails; // audit trail

    public SchedulerTask(String name, String schedule, String workflowAlias) {
        this.name = name;
        this.schedule = schedule;
        this.workflowAlias = workflowAlias;
        this.executionCount = new AtomicInteger();
    }

    public String getName() {
        return name;
    }

    public String getSchedule() {
        return schedule;
    }

    public String getWorkflowAlias() {
        return workflowAlias;
    }

    public int getExecutionCount() {
        return executionCount.get();
    }

    public ExecutionDetails getLastExecutionDetails() {
        return lastExecutionDetails;
    }

    public void setLastExecutionDetails(ExecutionDetails lastExecutionDetails) {
        this.lastExecutionDetails = lastExecutionDetails;
        this.executionCount.incrementAndGet();
    }
}
