package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.SchedulerTask;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EngineScheduler {

    private static final Logger logger = LoggerFactory.getLogger(EngineScheduler.class);
    private Scheduler scheduler;

    public EngineScheduler() {
        scheduler = new Scheduler();
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
        String taskId = scheduler.schedule(task.getSchedulingPattern(), new Runnable() {
            public void run() {
                task.execute();
            }
        });
        logger.info("EngineScheduler scheduled task {} with time interval {}", taskId, task.getSchedulingPattern());
        return taskId;
    }
}