package com.labs2160.slacker.core.engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.Trigger;
import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.NoArgumentsFoundException;
import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.ScheduledJob;
import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerRequest;
import com.labs2160.slacker.api.SlackerResponse;
import com.labs2160.slacker.core.event.WorkflowExecutionEvent;
import com.labs2160.slacker.core.event.WorkflowExecutionEventType;
import com.labs2160.slacker.core.event.WorkflowExecutionListener;

public class WorkflowEngineImpl implements WorkflowEngine {

    private final static String HELP_KEY = "help";

    private final static Logger logger = LoggerFactory.getLogger(WorkflowEngineImpl.class);

    private final static long INITIAL_SCHEDULE_DELAY_SEC = 10;

    /** scheduler for jobs */
    private ScheduledExecutorService scheduler;

    /** main executor service */
    private ExecutorService executorService;

    /** in-memory registry of all workflows */
    private final WorkflowRegistry registry;

    /** in-memory registry of all collectors */
    private final Map<String,RequestCollector> collectors;

    private final List<WorkflowExecutionListener> executionListeners;

    /** in-memory registry of all triggers */
    private final Map<String,Trigger> triggers;

    public WorkflowEngineImpl() {
        registry = new WorkflowRegistry();
        collectors = new ConcurrentHashMap<>();
        executionListeners = new ArrayList<>();
        triggers = new ConcurrentHashMap<>();
    }

    @Override
    public void start() {
        long start = System.currentTimeMillis();
        logger.debug("Starting engine...");

        executorService = Executors.newFixedThreadPool(5);// TODO: parameterize
        scheduler = Executors.newScheduledThreadPool(1);

        for (String collectorName : collectors.keySet()) {
            try {
                logger.debug("Starting collector: {}", collectorName);
                RequestCollector collector = collectors.get(collectorName);
                collector.start(this);

                ScheduledJob [] jobs = collector.getScheduledJobs();
                if (jobs != null) {
                    for (ScheduledJob job : jobs) {
                        logger.debug("Scheduling job for {} with period of {} s", collectorName, job.getPeriod());
                        ScheduledFuture<?> jobFuture = scheduler.scheduleAtFixedRate(
                                job, INITIAL_SCHEDULE_DELAY_SEC, job.getPeriod(), TimeUnit.SECONDS);
                        // TODO: keep track of jobFutures per collector if we want collectors to be shutdown at runtime
                    }
                }

            } catch (Exception e) {
                logger.error("Could not start collector {} due to error.", collectorName, e);
                logger.warn("Skipping collector {} but will continue startup.", collectorName);
            }
        }

        for (String triggerName : triggers.keySet()) {
            logger.debug("Starting trigger: {}", triggerName);
            Trigger trigger = triggers.get(triggerName);
            trigger.start(this);
        }
        logger.info("Engine started in {} ms", System.currentTimeMillis() - start);
    }

    @Override
    public void shutdown() {
        for (String collectorName : collectors.keySet()) {
            logger.debug("Shutting down collector: {}", collectorName);
            RequestCollector collector = collectors.get(collectorName);
            collector.shutdown();
        }
    }

    public void addCollector(String name, RequestCollector collector) {
        collectors.put(name, collector);
        logger.info("Collector \"{}\" added", name);
    }

    public void addWorkflow(Workflow wf, String ... path) {
        registry.addWorkflow(wf, path);
        logger.info("Added workflow: {} - {}", path, wf.getName());
    }

    public void addTrigger(String name, Trigger trigger) {
        triggers.put(name, trigger);
        logger.info("Trigger \"{}\" added", name);
    }

    @Override
    public void addWorkflowExecutionListener(WorkflowExecutionListener listener) {
        logger.info("Adding execution listener: {}", listener.getClass().getName());
        this.executionListeners.add(listener);
    }

    @Override
    public Future<SlackerResponse> handle(final SlackerRequest request) throws InvalidRequestException, NoArgumentsFoundException, SlackerException {
        return this.executorService.submit(new Callable<SlackerResponse>() {
            @Override
            public SlackerResponse call() throws Exception {
                SlackerContext ctx = handleHelp(request);
                if (ctx != null) {
                    return convertToImmutableResponse(ctx.getResponse());
                } else {
                    final WorkflowRequest wfr = parseWorkflowRequest(request.getRawArguments());
                    logger.debug("Request submitted: path={}, wf={}, args={}", wfr.getPath(), wfr.getWorkflow(), wfr.getArgs());
                    return executeWorkflow(wfr);
                }
            }
        });
    }

    private SlackerResponse executeWorkflow(WorkflowRequest wfr) throws SlackerException {
        Workflow wf = wfr.getWorkflow();
        if (wf == null) {
            throw new InvalidRequestException("Cannot find workflow for args: " + StringUtils.join(wfr.getPath(), " "));
        }

        final SlackerContext ctx = new SlackerContext(wfr.getPath(), wfr.getArgs());

        final String workflowId = getNewWorkflowId();

        if (listenersExist()) {
            notifyListeners(new WorkflowExecutionEvent(WorkflowExecutionEventType.WORKFLOW_START, workflowId, wfr, true, System.currentTimeMillis()));
        }
        for (Action action : wf.getActions()) {
            if (listenersExist()) {
                notifyListeners(new WorkflowExecutionEvent(WorkflowExecutionEventType.ACTION_START, workflowId, wfr, true, System.currentTimeMillis()));
            }
            boolean successful = action.execute(ctx);
            if (listenersExist()) {
                notifyListeners(new WorkflowExecutionEvent(WorkflowExecutionEventType.ACTION_FINISH, workflowId, wfr, successful, System.currentTimeMillis()));
            }
            if (! successful) {
                logger.error("Error encountered executing action: {}", action.getClass().getName());
                break; // stop execution
            }
        }

        SlackerResponse response = convertToImmutableResponse(ctx.getResponse());
        for (Endpoint endpoint : wf.getEndpoints()) {
            if (listenersExist()) {
                notifyListeners(new WorkflowExecutionEvent(WorkflowExecutionEventType.ENDPOINT_START, workflowId, wfr, true, System.currentTimeMillis()));
            }
            boolean successful = endpoint.deliverResponse(response);
            if (listenersExist()) {
                notifyListeners(new WorkflowExecutionEvent(WorkflowExecutionEventType.ENDPOINT_FINISH, workflowId, wfr, successful, System.currentTimeMillis()));
            }
            if (! successful) {
                logger.error("Error encountered executing endpoint: {}", endpoint.getClass().getName());
            }
        }

        if (listenersExist()) {
            notifyListeners(new WorkflowExecutionEvent(WorkflowExecutionEventType.WORKFLOW_FINISH, workflowId, wfr, true, System.currentTimeMillis()));
        }
        return response;
    }

    @Override
    public WorkflowRegistry getRegistry() {
        return registry;
    }

    private WorkflowRequest parseWorkflowRequest(String [] origArgs) throws InvalidRequestException {
        // For now, workflows are stored in a stupid hash so just greedily
        // find a matching workflow by concatenating args to form
        // possible paths.
        final RegistryNode match = registry.findWorkflowMatch(origArgs);
        if (match == null) {
            throw new InvalidRequestException("Cannot find workflow for args: " + StringUtils.join(origArgs, " "));
        }

        String [] path = match.getPath();
        String [] args = origArgs.length > path.length ? Arrays.copyOfRange(origArgs, path.length, origArgs.length) : null;

        return new WorkflowRequest(path, args, match.getWorkflow());
    }

    /**
     * TODO: move this out of the engine.
     * @param request
     * @return
     */
    private SlackerContext handleHelp(SlackerRequest request) {
        if (request.getRawArguments()[0].equals(HELP_KEY)) {
            List<WorkflowMetadata> metadata = registry.getWorkflowMetadata();
            Collections.sort(metadata, new Comparator<WorkflowMetadata>() {
                @Override
                public int compare(WorkflowMetadata m1, WorkflowMetadata m2) {
                    String p1 = StringUtils.join(m1.getPath(), "::");
                    String p2 = StringUtils.join(m2.getPath(), "::");
                    return p1.compareTo(p2);
                }
            });
            SlackerContext ctx = new SlackerContext(new String[]{HELP_KEY}, null);
            StringBuilder sb = new StringBuilder("I can understand:\n");
            for (WorkflowMetadata wm : metadata) {
                sb.append(StringUtils.join(wm.getPath(), " "))
                    .append(" ")
                    .append(trimToEmpty(wm.getArgsSpecification()))
                    .append("\n").append("    ")
                    .append(trimToEmpty(wm.getName()))
                    .append(" - ").append(trimToEmpty(wm.getDescription()))
                    .append("\n");
            }
            ctx.setResponseMessage(sb.toString());
            return ctx;
        }
        return null;
    }

    private boolean listenersExist() {
        return ! executionListeners.isEmpty();
    }

    private void notifyListeners(WorkflowExecutionEvent event) {
        for (WorkflowExecutionListener listener : executionListeners) {
            try {
                listener.notifyEvent(event);
            } catch (Exception e) {
                logger.error("Failed to notify listener of event {} : {}", event, e);
            }
        }
    }

    // TODO: use a GUID
    private String getNewWorkflowId() {
        return "WF" + System.currentTimeMillis();
    }

    private SlackerResponse convertToImmutableResponse(SlackerResponse res) {
        return new SlackerResponse(res.getMessage(), res.getAttachedMedia(), res.getAttachedMediaType()) {

            @Override
            public void setMessage(String message) {
                throw new UnsupportedOperationException("Cannot set message on this response object");
            }

            @Override
            public void setAttachedMedia(InputStream attachedMedia) {
                throw new UnsupportedOperationException("Cannot set message on this response object");
            }

            @Override
            public void setAttachedMediaType(String attachedMediaType) {
                throw new UnsupportedOperationException("Cannot set message on this response object");
            }
        };
    }

    private String trimToEmpty(String orig) {
        return orig == null ? "" : orig.trim();
    }
}
