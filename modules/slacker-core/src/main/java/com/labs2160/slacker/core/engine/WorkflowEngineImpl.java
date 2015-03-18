package com.labs2160.slacker.core.engine;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.NoArgumentsFoundException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.Response;
import com.labs2160.slacker.api.ScheduledJob;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerContext;

public class WorkflowEngineImpl implements WorkflowEngine {

	private final static String HELP_KEY = "help";

	private final static Logger logger = LoggerFactory.getLogger(WorkflowEngineImpl.class);

	private final static long INITIAL_SCHEDULE_DELAY_SEC = 10;

	/** scheduler for jobs */
	private ScheduledExecutorService scheduler;

	/** in-memory registry of all workflows */
	private final WorkflowRegistry registry;

	/** in-memory registry of all collectors */
	private final Map<String,RequestCollector> collectors;

	private class WorkflowRequest {
		private String [] path;
		private String [] args;
		private Workflow workflow;

		public WorkflowRequest(String [] path, String [] args, Workflow wf) {
			this.path = path;
			this.args = args;
			this.workflow = wf;
		}

		public String [] getPath() {
			return path;
		}

		public String [] getArgs() {
			return args;
		}

		public Workflow getWorkflow() {
			return workflow;
		}
	}

	public WorkflowEngineImpl() {
		registry = new WorkflowRegistry();
		collectors = new ConcurrentHashMap<>();
	}

	@Override
	public void start() {
		long start = System.currentTimeMillis();
		logger.debug("Starting engine...");

		scheduler = Executors.newScheduledThreadPool(1);

		for (String collectorName : collectors.keySet()) {
			try {
				logger.debug("Starting collector: {}", collectorName);
				RequestCollector collector = collectors.get(collectorName);
				collector.start(this);

				ScheduledJob [] jobs = collector.getScheduledJobs();
				if (jobs != null) {
					for (ScheduledJob job : jobs) {
						logger.debug("Scheduling job for {} with perio of {} s", collectorName, job.getPeriod());
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

	public void addCollector(String path, RequestCollector collector) {
		collectors.put(path, collector);
		logger.info("Collector \"{}\" added", path);
	}

	public void addWorkflow(Workflow wf, String ... path) {
		registry.addWorkflow(wf, path);
		logger.info("Added workflow: {} - {}", path, wf.getName());
	}

	@Override
	public SlackerContext handle(Request request) throws InvalidRequestException, NoArgumentsFoundException, SlackerException {
		SlackerContext ctx = handleHelp(request);
		if (ctx != null) {
			return ctx;
		} else {
			WorkflowRequest wfr = parseWorkflowRequest(request.getRawArguments());
			logger.debug("Request submitted: path={}, wf={}, args={}", wfr.getPath(), wfr.getWorkflow(), wfr.getArgs());
			Workflow wf = wfr.getWorkflow();
			if (wf == null) {
				throw new InvalidRequestException("Cannot find workflow for args: " + StringUtils.join(wfr.getPath(), " "));
			}

			ctx = new SlackerContext(wfr.getPath(), wfr.getArgs());
			for (Action action : wf.getActions()) {
				if (! action.execute(ctx)) {
					logger.error("Error enountered executing action: {}", action.getClass().getName());
				}
			}

			Response response = convertToImmutableResponse(ctx.getResponse());
			for (Endpoint endpoint : wf.getEndpoints()) {
				if (! endpoint.deliverResponse(response)) {
					logger.error("Error enountered executing endpoint: {}", endpoint.getClass().getName());
				}
			}
			return ctx;
		}
	}

	@Override
	public WorkflowRegistry getRegistry() {
		return null;
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

	private SlackerContext handleHelp(Request request) {
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
			StringBuilder sb = new StringBuilder();
			for (WorkflowMetadata wm : metadata) {
				sb.append(StringUtils.join(wm.getPath(), " "))
					.append(" ")
					.append(wm.getArgsSpecification()).append("\n")
					.append("\t").append(wm.getName()).append(" - ").append(wm.getDescription())
					.append("\n");
			}
			ctx.setResponseMessage(sb.toString());
			return ctx;
		}
		return null;
	}

	private Response convertToImmutableResponse(Response res) {
		return new Response(res.getMessage(), res.getAttachedMedia(), res.getAttachedMediaType()) {

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
}
