package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerRequest;
import com.labs2160.slacker.api.response.SlackerOutput;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class EngineScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EngineScheduler.class);

    private final WorkflowEngine workflowEngine;
    private final Scheduler scheduler;
    private final Map<String, SchedulerTask> scheduledTasks;

    public EngineScheduler(WorkflowEngine workflowEngine) {
        this.workflowEngine = workflowEngine;
        scheduler = new Scheduler();
        scheduledTasks = new HashMap<>();
    }

    public void start() {
        scheduler.start();
        logger.info("EngineScheduler started");
    }

    public void stop() {
        scheduler.stop();
        logger.info("EngineScheduler stopped");
    }

    public Set<String> getScheduledTaskIds() {
        return Collections.unmodifiableSet(scheduledTasks.keySet());
    }

    public void restart() {
        stop();
        start();
    }

    /**
     * @param task
     * @return Task ID
     */
    public String schedule(final SchedulerTask task) {
        String taskId = scheduler.schedule(task.getSchedule(), new Runnable() {
            public void run() {
                try {
                    logger.debug("Firing task \"{}\" (count={})", task.getName(), task.getExecutionCount());
                    SlackerRequest request = new SlackerRequest(EngineScheduler.class.getSimpleName(), task.getWorkflowAlias().split(","));
                    Future<ExecutionDetails> executionDetails = workflowEngine.handleRequest(request);

                    task.setLastExecutionDetails(executionDetails.get());
                } catch (SlackerException e) {
                    logger.error("Error executing schedule \"" + task.getName() + "\"", e);
                } catch (InterruptedException e) {
                    logger.error("Error executing schedule \"" + task.getName() + "\"", e);
                } catch (ExecutionException e) {
                    logger.error("Error executing schedule \"" + task.getName() + "\"", e);
                }
            }
        });
        scheduledTasks.put(taskId, task);
        logger.info("EngineScheduler scheduled task \"{}\" id={} with schedule: {}", task.getName(), taskId, task.getSchedule());
        return taskId;
    }
}