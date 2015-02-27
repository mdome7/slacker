package com.labs2160.slacker.core.engine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;

public class WorkflowEngineImpl implements WorkflowEngine {
	
	private static Logger logger = LoggerFactory.getLogger(WorkflowEngineImpl.class);

	/** in-memory registry of all workflows */
	private Map<String,Workflow> workflows = new ConcurrentHashMap<>();

	/** in-memory registry of all collectors */
	private Map<String,RequestCollector> collectors = new ConcurrentHashMap<>();

	@Override
	public void start() {
		long start = System.currentTimeMillis();
		logger.debug("Starting engine...");
		for (String collectorName : collectors.keySet()) {
			logger.debug("Starting collector: {}", collectorName);
			RequestCollector collector = collectors.get(collectorName);
			collector.start();
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
	public void submitRequest(String key, String... args) throws InvalidRequestException, WorkflowException {
		logger.debug("Request submitted: key={}, args={}", key, args);
		Workflow wf = workflows.get(key);
		if (wf == null) {
			throw new InvalidRequestException("Cannot find workflow for key: " + key);
		}
		
		WorkflowContext ctx = new WorkflowContext(key, args);
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
	}
}
