package com.labs2160.slacker.plugin.weather;

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;
import com.labs2160.slacker.plugin.misc.yahoo.YahooResponse;

/**
 * Arguments: zip_code - 
 * Output Message: weather prediction for today
 */
public class WeatherAction implements Action {
	
	private static Logger logger = LoggerFactory.getLogger(WeatherAction.class);

	private WebTarget target;

	public WeatherAction() {	
		Client client = ClientBuilder.newClient(new ClientConfig());
		target = client.target("https://query.yahooapis.com/v1/public/yql");
	}
	
	@Override
	public boolean execute(WorkflowContext ctx) throws WorkflowException {
		try {
			if (ctx.getWorkflowArgs() == null || ctx.getWorkflowArgs().length == 0) {
				throw new WorkflowException("Argument is required");
			}
			final String input = ctx.getWorkflowArgs()[0].replaceAll("\"", "");
			String yql = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")",
					input);
			logger.debug(yql);
	
			String json = target.queryParam("q", yql).queryParam("format", "json")
			.queryParam("env", "store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
			.request().get(String.class);
	
			logger.info("Response: {}", json);
			ObjectMapper mapper = new ObjectMapper();
			YahooResponse<WeatherResults> response = mapper.readValue(json, new TypeReference<YahooResponse<WeatherResults>>() {});
	
			WeatherResults results = response.getResults();
			ctx.setResponseMessage(results != null ? results.toString() : "Sorry, cannot retrieve weather for: " + input );
		
		return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
