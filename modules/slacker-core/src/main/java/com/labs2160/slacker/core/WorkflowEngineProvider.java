package com.labs2160.slacker.core;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.hipchat.HipChatCollector;
import com.labs2160.slacker.api.misc.EchoAction;
import com.labs2160.slacker.api.misc.PrintToConsoleEndpoint;
import com.labs2160.slacker.api.misc.StockAction;
import com.labs2160.slacker.api.weather.WeatherAction;
import com.labs2160.slacker.core.engine.Workflow;
import com.labs2160.slacker.core.engine.WorkflowEngine;
import com.labs2160.slacker.core.engine.WorkflowEngineImpl;

/**
 * TODO: convert into a CDI "provider"
 * @author mike
 *
 */
@ApplicationScoped
public class WorkflowEngineProvider {
	private static final Logger logger = LoggerFactory.getLogger(WorkflowEngineProvider.class);

	@Produces @ApplicationScoped @Named("engine")
	public WorkflowEngine provide() {
		final long start = System.currentTimeMillis();
		WorkflowEngineImpl engine = new WorkflowEngineImpl();
		initializeListeners(engine);
		initializeWorkflows(engine);
		logger.debug("Engine initialized in {} ms", System.currentTimeMillis() - start);
		return engine;
	}
	
	private void initializeListeners(WorkflowEngineImpl engine) {
		HipChatCollector hcListener = new HipChatCollector();
		engine.addRequestListener("HipChat", hcListener);
	}
	
	private void initializeWorkflows(WorkflowEngineImpl engine) {
		PrintToConsoleEndpoint p2c = new PrintToConsoleEndpoint();
		
		Workflow wf1 = new Workflow();
		wf1.addAction(new EchoAction());
		wf1.addEndpoint(p2c);
		engine.addWorkflow("echo2console", wf1);

		Workflow wf2 = new Workflow();
		wf2.addAction(new StockAction());
		wf2.addEndpoint(p2c);
		engine.addWorkflow("stock", wf2);

		Workflow wf3 = new Workflow();
		wf3.addAction(new WeatherAction());
		wf3.addEndpoint(p2c);
		engine.addWorkflow("weather", wf3);
	}
	
}
