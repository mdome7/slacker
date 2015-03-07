package com.labs2160.slacker.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.core.engine.Workflow;
import com.labs2160.slacker.core.engine.WorkflowEngine;
import com.labs2160.slacker.core.engine.WorkflowEngineImpl;
import com.labs2160.slacker.plugin.hipchat.HipChatCollector;
import com.labs2160.slacker.plugin.misc.EchoAction;
import com.labs2160.slacker.plugin.misc.PrintToConsoleEndpoint;
import com.labs2160.slacker.plugin.misc.StockAction;
import com.labs2160.slacker.plugin.weather.WeatherAction;

/**
 * TODO: convert into a CDI "provider"
 * @author mike
 *
 */
@ApplicationScoped
public class WorkflowEngineProvider {
	private static final Logger logger = LoggerFactory.getLogger(WorkflowEngineProvider.class);

	@Produces @ApplicationScoped @Named("engine")
	public WorkflowEngine provide() throws IOException {
		// TODO: inject config and explore other Configuration classes, format (YML)
		Properties config = new Properties();
		config.load(new FileInputStream("/app-data/slacker/slacker.properties"));
		
		final long start = System.currentTimeMillis();
		WorkflowEngineImpl engine = new WorkflowEngineImpl();
		initializeCollectors(engine, config);
		initializeWorkflows(engine, config);
		logger.debug("Engine initialized in {} ms", System.currentTimeMillis() - start);
		return engine;
	}
	
	private void initializeCollectors(WorkflowEngineImpl engine, Properties config) {
		HipChatCollector hcListener = new HipChatCollector(config.getProperty("xmpp.host"),
				config.getProperty("xmpp.user"), config.getProperty("xmpp.password"));
		engine.addCollector("HipChat", hcListener);
	}
	
	private void initializeWorkflows(WorkflowEngineImpl engine, Properties config) {
		PrintToConsoleEndpoint p2c = new PrintToConsoleEndpoint();
		
		Workflow wf1 = new Workflow("Echo", "Spits back what you tell it");
		wf1.addAction(new EchoAction());
		wf1.addEndpoint(p2c);
		engine.addWorkflow("echo", wf1);

		Workflow wf2 = new Workflow("Stock Price", "Gets the current stock price for the specified symbol");
		wf2.addAction(new StockAction());
		wf2.addEndpoint(p2c);
		engine.addWorkflow("stock", wf2);

		Workflow wf3 = new Workflow("Weather", "Gets the current weather for the specified location");
		wf3.addAction(new WeatherAction());
		wf3.addEndpoint(p2c);
		engine.addWorkflow("weather", wf3);
	}
	
}
