package com.labs2160.slacker.core;

import java.io.FileInputStream;
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
import com.labs2160.slacker.plugin.misc.MarkitStockAction;
import com.labs2160.slacker.plugin.misc.PrintToConsoleEndpoint;
import com.labs2160.slacker.plugin.misc.RandomPickerAction;
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
        final String configPath = System.getProperty("config","slacker.properties");
        logger.info("Loading config file: {}", configPath);
        config.load(new FileInputStream(configPath));

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

        Workflow wfEcho = new Workflow("Echo", "Echoes messages back");
        wfEcho.setArgsSpecification("<any string>");
        wfEcho.setDescription("Echoes messages back");
        wfEcho.addAction(new EchoAction());
        wfEcho.addEndpoint(p2c);
        engine.addWorkflow(wfEcho, "echo");

        Workflow wfStock = new Workflow("Stock Price", "Retrieves stock price for the given symbol");
        wfStock.setArgsSpecification("<stock symbol>");
        wfStock.addAction(new MarkitStockAction());
        wfStock.addEndpoint(p2c);
        engine.addWorkflow(wfStock, "stock");
        engine.addWorkflow(wfStock, "stock", "price");

        Workflow wfWeather = new Workflow("Weather", "Gets the current weather for the specified location");
        wfWeather.setArgsSpecification("<location or zip code>");
        wfWeather.setExampleArgs("\"Seattle, WA\" or 98012");
        wfWeather.addAction(new WeatherAction());
        wfWeather.addEndpoint(p2c);
        engine.addWorkflow(wfWeather, "weather");

        Workflow wfRandomPicker = new Workflow("Random Picker", "Picks random values from a set");
        wfRandomPicker.setArgsSpecification("<num to pick> <choice 1> <choice 2> ... <choice N>");
        wfRandomPicker.setExampleArgs("2 apple banana orange");
        wfRandomPicker.addAction(new RandomPickerAction());
        wfRandomPicker.addEndpoint(p2c);
        engine.addWorkflow(wfRandomPicker, "pick");
    }

}
