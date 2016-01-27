package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.SlackerRequest;
import com.labs2160.slacker.api.SlackerResponse;
import com.labs2160.slacker.api.Trigger;
import it.sauronsoftware.cron4j.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.Future;

public class ScheduleTrigger implements Trigger {

    private static final Logger logger = LoggerFactory.getLogger(Trigger.class);
    private static final Scheduler scheduler = new Scheduler();

    private String schedulingPattern;
    private String request;

    @Override
    public void setConfiguration(Properties config) {
        this.schedulingPattern = config.getProperty("schedulingPattern");
        this.request = config.getProperty("request");
    }

    @Override
    public void start(final RequestHandler handler) {
        scheduler.schedule(schedulingPattern, new Runnable() {
            public void run() {
                logger.info("Trigger breakpoint reached. Processing request '{}' now.", request);
                try {
                    String[] tokens = request.split(" ");
                    SlackerRequest sr = new SlackerRequest("trigger", tokens);
                    Future<SlackerResponse> future = handler.handle(sr);
                    SlackerResponse response = future.get();
                    logger.info("Request {} succeeded with response {}", request, response);
                } catch (Exception e) {
                    logger.error("Could not process request '{}' due to error {}.", request, e);
                }
            }
        });
        scheduler.start();
    }
}