package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.response.SlackerOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

public class SchedulerTrigger implements Trigger {

    private static final Logger logger = LoggerFactory.getLogger(Trigger.class);

    private String schedulingPattern;
    private String request;
    private RequestHandler handler;

    @Override
    public void setComponents(Map<String, Resource> resources, Properties config) {
        this.schedulingPattern = config.getProperty("schedulingPattern");
        this.request = config.getProperty("request");
    }

    @Override
    public void start(final RequestHandler handler) {
        this.handler = handler;
    }

    @Override
    public SchedulerTask[] getSchedulerTasks() {
        SchedulerTask configRequest = new SchedulerTask(schedulingPattern) {
            @Override
            public void execute() {
                logger.info("Trigger breakpoint reached. Processing request '{}' now.", request);
                try {
                    String[] tokens = request.split(" ");
                    SlackerRequest sr = new SlackerRequest("trigger", tokens);
                    Future<SlackerOutput> future = handler.handle(sr);
                    SlackerOutput response = future.get();
                    logger.info("Request {} succeeded with response {}", request, response);
                } catch (Exception e) {
                    logger.error("Could not process request '{}' due to error {}.", request, e);
                }
            }
        };
        return new SchedulerTask[] { configRequest };
    }
}