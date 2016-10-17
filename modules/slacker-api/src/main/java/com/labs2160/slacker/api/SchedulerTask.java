package com.labs2160.slacker.api;

public abstract class SchedulerTask {

    private final String schedulingPattern;

    public SchedulerTask(String schedulingPattern) {
        this.schedulingPattern = schedulingPattern;
    }

    public String getSchedulingPattern() {
        return schedulingPattern;
    }

    abstract public void execute();
}
