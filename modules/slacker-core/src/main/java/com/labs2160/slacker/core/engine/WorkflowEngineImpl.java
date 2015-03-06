package com.labs2160.slacker.core.engine;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;

public class WorkflowEngineImpl implements WorkflowEngine {
	
	private final static Logger logger = LoggerFactory.getLogger(WorkflowEngineImpl.class);

	/** in-memory registry of all workflows */
	private Map<String,Workflow> workflows = new ConcurrentHashMap<>();

	/** in-memory registry of all collectors */
	private Map<String,RequestCollector> collectors = new ConcurrentHashMap<>();
	
	private class WorkflowRequest {
		private String key;
		private String [] args;
		private Workflow workflow;

		public WorkflowRequest(String key, String [] args, Workflow wf) {
			this.key = key;
			this.args = args;
			this.workflow = wf;
		}
		
		public String getKey() {
			return key;
		}

		public String[] getArgs() {
			return args;
		}

		public Workflow getWorkflow() {
			return workflow;
		}
	}

	@Override
	public void start() {
		long start = System.currentTimeMillis();
		logger.debug("Starting engine...");
		for (String collectorName : collectors.keySet()) {
			try {
				logger.debug("Starting collector: {}", collectorName);
				RequestCollector collector = collectors.get(collectorName);
				collector.start(this);
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
	
	public void addRequestListener(String key, RequestCollector collector) {
		collectors.put(key, collector);
		logger.info("Collector \"{}\" added", key);
	}
	
	public void addWorkflow(String key, Workflow wf) {
		workflows.put(key, wf);
		logger.info("Workflow \"{}\" added", key);
	}

	@Override
	public WorkflowContext handle(Request request) throws InvalidRequestException, WorkflowException {
		WorkflowRequest wfr = parseWorkflowRequest(request.getArgs());
		logger.debug("Request submitted: key={}, wf={}, args={}", wfr.getKey(), wfr.getWorkflow(), wfr.getArgs());
		Workflow wf = wfr.getWorkflow();
		if (wf == null) {
			throw new InvalidRequestException("Cannot find workflow for key: " + wfr.getKey());
		}
		
		WorkflowContext ctx = new WorkflowContext(wfr.getKey(), wfr.getArgs());
		for (Action action : wf.getActions()) {
			if (! action.execute(ctx)) {
				logger.error("Error enountered executing action: {}", action.getClass().getName());
			}
		}

		for (Endpoint endpoint : wf.getEndpoints()) {
			if (! endpoint.execute(ctx)) {
				logger.error("Error enountered executing endpoint: {}", endpoint.getClass().getName());
			}
		}
		return ctx;
	}

	@Override
	public WorkflowMetadata [] getWorkflowMetadata() {
		WorkflowMetadata [] metadata = new WorkflowMetadata[workflows.size()];
		int i = 0;
		for (String key : workflows.keySet()) {
			Workflow wf = workflows.get(key);
			metadata[i++] = new WorkflowMetadata(key, wf.getName(), wf.getDescription(), wf.getExampleArgs());
		}
		// TODO Auto-generated method stub
		return metadata;
	}
	
	private WorkflowRequest parseWorkflowRequest(String [] origArgs) throws InvalidRequestException {
		// For now, workflows are stored in a stupid hash so just greedily
		// find a matching workflow by concatenating args to form
		// possible keys.
		Workflow wf = null;
		String key = origArgs[0];
		String [] args = origArgs.length > 1 ? Arrays.copyOfRange(origArgs, 1, origArgs.length) : null;
		wf = workflows.get(key);
		if (wf == null) {
			throw new InvalidRequestException("Cannot find workflow for key " + key);
		}
		
		return new WorkflowRequest(key, args, wf);
	}
}
