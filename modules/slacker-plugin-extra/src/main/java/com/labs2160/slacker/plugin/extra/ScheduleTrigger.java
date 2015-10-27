package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.Trigger;
import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.SlackerRequest;
import com.labs2160.slacker.api.SlackerResponse;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScheduleTrigger implements Trigger {

    private static final Logger logger = LoggerFactory.getLogger(Trigger.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String interval;
    private String[] requests;

    public ScheduleTrigger() { }

    @Override
    public void setConfiguration(Properties config) {
        this.interval = config.getProperty("interval");
        this.requests = config.getProperty("requests").split(";");
    }

    @Override
    public void start(RequestHandler handler) {
        TimerTask tt = new TriggerTask(handler);
        TimeUnit tu;
        switch (interval) {
            case "second": tu = TimeUnit.SECONDS; break;
            case "minute": tu = TimeUnit.MINUTES; break;
            case "hour": tu = TimeUnit.HOURS; break;
            case "day": tu = TimeUnit.DAYS; break;
            default: tu = TimeUnit.MILLISECONDS; break;
        }
        scheduler.scheduleAtFixedRate(tt, 0, 1, tu);
    }

    private class TriggerTask extends TimerTask {
        private RequestHandler handler;

        public TriggerTask(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        public void run() {
            logger.info("Trigger interval {} breakpoint reached. Starting its workflow now.", interval);
            for (String request : requests) {
                try {
                    logger.info("Attempting request {}", request);
                    String[] tokens = request.split(" ");
                    SlackerRequest sr = new SlackerRequest("trigger", tokens);
                    Future<SlackerResponse> future = handler.handle(sr);
                    SlackerResponse response = future.get();
                    logger.info("Request {} succeeded with response {}", request, response);
                } catch (Exception e) {
                    logger.error("Could not run trigger due to error {}.", e);
                }
            }
            logger.info("Trigger completed");
        }
    }
}
