package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerRequest;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EngineScheduler.class);

    private final RequestHandler requestHandler;
    private final Scheduler scheduler;

    public EngineScheduler(RequestHandler requestHandler) {
        scheduler = new Scheduler();
        this.requestHandler = requestHandler;
    }

    public void start() {
        scheduler.start();
        logger.info("EngineScheduler started");
    }

    public void stop() {
        scheduler.stop();
        logger.info("EngineScheduler stopped");
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
                    SlackerRequest request = new SlackerRequest(EngineScheduler.class.getSimpleName(), task.getWorkflowAlias().split(","));
                    requestHandler.handle(request);
                } catch (SlackerException e) {
                    logger.error("Error executing schedule \"" + task.getName() + "\"", e);
                }
            }
        });
        logger.info("EngineScheduler scheduled task {} with schedule: {}", taskId, task.getSchedule());
        return taskId;
    }
}