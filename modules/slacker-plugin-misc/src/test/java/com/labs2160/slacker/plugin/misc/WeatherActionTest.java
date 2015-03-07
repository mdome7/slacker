package com.labs2160.slacker.plugin.misc;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;
import com.labs2160.slacker.plugin.misc.yahoo.YahooResponse;
import com.labs2160.slacker.plugin.weather.WeatherAction;
import com.labs2160.slacker.plugin.weather.WeatherResults;
import com.labs2160.slacker.plugin.weather.WeatherResults.Forecast;

public class WeatherActionTest {

	private static final Logger logger = LoggerFactory.getLogger(WeatherActionTest.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	private WeatherAction action;
	
	@Before
	public void before() {
		action = new WeatherAction();
	}

	@Test
	public void testJsonSerialization() throws IOException {
		Map<String, Object> map = new HashMap<>();
		map.put("a", "1");
		WeatherResults results = new WeatherResults();
		//results.setChannel(map);
		
		String json = mapper.writeValueAsString(results);
		logger.info("Serialized:\n{}", json);
	}
	
	@Test
	public void testJsonDeserialization() throws IOException {
		URL resource = getClass().getClassLoader().getResource("weatherResponse.json");
		YahooResponse<WeatherResults> response = mapper.readValue(resource.openStream(), new TypeReference<YahooResponse<WeatherResults>>() {});

		WeatherResults results = response.getResults();
		logger.info(results.toString());
		for (Forecast fc : results.getForecast()) {
			logger.info("\t{}: {} to {} - {}", fc.date, fc.low, fc.high, fc.text);
		}
	}
	
	@Test
	public void testExecute() {
		try {
			logger.info(getWeather("11554"));
		} catch (WorkflowException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getWeather(String text) throws WorkflowException {
		WorkflowContext ctx = new WorkflowContext("Weather", text);
		Assert.assertTrue(action.execute(ctx));
		return ctx.getResponseMessage();
	}
}
