package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;
import com.labs2160.slacker.core.event.WorkflowExecutionEvent;
import com.labs2160.slacker.core.event.WorkflowExecutionEventType;
import com.labs2160.slacker.core.event.WorkflowExecutionListener;
import com.labs2160.slacker.core.util.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class WorkflowEngineImpl implements WorkflowEngine {

    private final static String HELP_KEY = "help";

    private final static Logger logger = LoggerFactory.getLogger(WorkflowEngineImpl.class);

    /** scheduler for jobs */
    private EngineScheduler scheduler;

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

        executorService = Executors.newFixedThreadPool(5); // TODO: parameterize
        scheduler = new EngineScheduler();

        for (String collectorName : collectors.keySet()) {
            try {
                logger.debug("Starting collector: {}", collectorName);
                RequestCollector collector = collectors.get(collectorName);
                collector.start(this);

                SchedulerTask [] tasks = collector.getSchedulerTasks();
                if (tasks != null) {
                    for (SchedulerTask task : tasks) {
                        scheduler.schedule(task);
                    }
                }

            } catch (Exception e) {
                logger.error("Could not start collector {} due to error.", collectorName, e);
                logger.warn("Skipping collector {} but will continue startup.", collectorName);
            }
        }

        for (String triggerName : triggers.keySet()) {
            try {
                logger.debug("Starting trigger: {}", triggerName);
                Trigger trigger = triggers.get(triggerName);
                trigger.start(this);

                SchedulerTask [] tasks = trigger.getSchedulerTasks();
                if (tasks != null) {
                    for (SchedulerTask task : tasks) {
                        scheduler.schedule(task);
                    }
                }
            } catch (Exception e) {
                logger.error("Could not start trigger {} due to error.", triggerName, e);
                logger.warn("Skipping trigger {} but will continue startup.", triggerName);
            }
        }
        scheduler.start();
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
    public Future<SlackerOutput> handle(final SlackerRequest request) throws SlackerException {
        return this.executorService.submit(new Callable<SlackerOutput>() {
            @Override
            public SlackerOutput call() throws Exception {
                if (request.getRawArguments()[0].equals(HELP_KEY)) {
                    return handleHelp();
                } else {
                    final WorkflowRequest wfr = parseWorkflowRequest(request.getRawArguments());
                    logger.debug("Request submitted: path={}, wf={}, args={}", wfr.getPath(), wfr.getWorkflow(), wfr.getArgs());
                    return executeWorkflow(wfr);
                }
            }
        });
    }

    private SlackerOutput executeWorkflow(WorkflowRequest wfr) throws SlackerException {
        Workflow wf = wfr.getWorkflow();
        SlackerOutput output = null;

        SlackerContext ctx = new SlackerContext(wfr.getPath(), wfr.getArgs());

        final String workflowId = UUIDUtil.generateRandomUUID();

        boolean workflowSuccessful = false;
        try {
            notifyListeners(WorkflowExecutionEventType.WORKFLOW_START, workflowId, wfr, true);

            final List<Action> actions = wf.getActions();
            for (int i = 0; i < actions.size(); i++) {
                if (i > 0) { // propagate output from previous action to the next one
                    if (output instanceof TextOutput) {
                        ctx = new SlackerContext(ctx.getRequestPath(), ((TextOutput) output).getMessage().split(" "));
                    } else {
                        throw new IllegalStateException("Output from action #" + i +
                                " must be of type TextOutput so it can be propagated to subsequent actions - actual output type (" +
                                (output == null ? output.getClass().getName() : "<null>") + // shouldn't be null but just in case
                                ") not yet supported.");
                    }
                }

                final Action action = actions.get(i);

                notifyListeners(WorkflowExecutionEventType.ACTION_START, workflowId, wfr, true);
                boolean successful = false;
                try {
                    output = action.execute(ctx);
                    if (output == null) {
                        throw new IllegalStateException("Action " + action.getClass().getName() + " returned null output");
                    }
                    successful = true;
                } finally {
                    notifyListeners(WorkflowExecutionEventType.ACTION_FINISH, workflowId, wfr, successful);
                }
            }

            for (Endpoint endpoint : wf.getEndpoints()) {
                notifyListeners(WorkflowExecutionEventType.ENDPOINT_START, workflowId, wfr, true);
                boolean successful = false;
                try {
                    successful = endpoint.deliverResponse(output);
                } finally {
                    notifyListeners(WorkflowExecutionEventType.ENDPOINT_FINISH, workflowId, wfr, successful);
                }
            }
            workflowSuccessful = true;
        } finally {
            notifyListeners(WorkflowExecutionEventType.WORKFLOW_FINISH, workflowId, wfr, workflowSuccessful);
        }
        return output;
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
        if (match == null || match.getWorkflow() == null) {
            throw new InvalidRequestException("Cannot find workflow for args: " + StringUtils.join(origArgs, " "));
        }

        String [] path = match.getPath();
        String [] args = origArgs.length > path.length ? Arrays.copyOfRange(origArgs, path.length, origArgs.length) : null;
        return new WorkflowRequest(path, args, match.getWorkflow());
    }

    /**
     * TODO: move this out of the engine.
     * @return
     */
    private SlackerOutput handleHelp() {
        logger.debug("Handling help request");
        List<WorkflowMetadata> metadata = registry.getWorkflowMetadata();
        Collections.sort(metadata, new Comparator<WorkflowMetadata>() {
            @Override
            public int compare(WorkflowMetadata m1, WorkflowMetadata m2) {
                String p1 = StringUtils.join(m1.getPath(), "::");
                String p2 = StringUtils.join(m2.getPath(), "::");
                return p1.compareTo(p2);
            }
        });
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
        return new TextOutput(sb.toString());
    }

    private boolean listenersExist() {
        return ! executionListeners.isEmpty();
    }

    private void notifyListeners(WorkflowExecutionEventType eventType, String workflowId, WorkflowRequest wfr, boolean successful) {
        if (!successful) {
            logger.warn("Workflow {} encountered non-successful event {} - path: {}, args: {}", workflowId, eventType, wfr.getPath(), wfr.getArgs());
        }
        if (listenersExist()) {
            WorkflowExecutionEvent event = new WorkflowExecutionEvent(eventType, workflowId, wfr, successful, System.currentTimeMillis());
            for (WorkflowExecutionListener listener : executionListeners) {
                try {
                    listener.notifyEvent(event);
                } catch (Exception e) {
                    logger.error("Failed to notify listener of event {} : {}", event, e);
                }
            }
        }
    }

    private String trimToEmpty(String orig) {
        return orig == null ? "" : orig.trim();
    }
}
