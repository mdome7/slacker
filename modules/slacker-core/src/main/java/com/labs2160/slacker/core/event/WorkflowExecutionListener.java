package com.labs2160.slacker.core.event;

public interface WorkflowExecutionListener {

    /** an event happened */
    void notifyEvent(WorkflowExecutionEvent e);
}
