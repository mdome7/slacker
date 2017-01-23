package com.labs2160.slacker.core.engine;

public class SchedulerTask {

    private final String name;

    private final String schedule;

    private final String workflowAlias;

    public SchedulerTask(String name, String schedule, String workflowAlias) {
        this.name = name;
        this.schedule = schedule;
        this.workflowAlias = workflowAlias;
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
}
