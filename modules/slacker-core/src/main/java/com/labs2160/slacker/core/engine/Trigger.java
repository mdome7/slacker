package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.Configurable;
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

public class Trigger implements Configurable {

    private static final Logger logger = LoggerFactory.getLogger(Trigger.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String name;
    private String interval;
    private Workflow wf;
    private String[] requests;

    public Trigger(String name, String interval, Workflow wf, String requests) {
        this.name = name;
        this.interval = interval;
        this.wf = wf;
        this.requests = requests.split(";");
    }

    @Override
    public void setConfiguration(Properties config) {
        // do nothing
    }

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
            logger.info("Interval {} breakpoint reached for trigger {}. Starting its workflow now.", interval, name);
            for (String request : requests) {
                try {
                    logger.info("Attempting request {}", request);
                    String[] tokens = request.split(" ");
                    SlackerRequest sr = new SlackerRequest(name, tokens);
                    Future<SlackerResponse> future = handler.handle(sr);
                    SlackerResponse response = future.get();
                    logger.info("Request {} succeeded with response {}", request, response);
                } catch (Exception e) {
                    logger.error("Could not start trigger {} due to error.", name, e);
                    logger.warn("Skipping collector {} but will continue startup.", name);
                }
            }
            logger.info("Trigger {} succeeded");
        }
    }
}
