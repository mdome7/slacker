package com.labs2160.slacker.plugin.extra.yahoo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.annotation.ActionDescription;
import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Arguments: zip_code or city
 * Output Message: weather prediction for today
 */
@ActionDescription(
        name = "Weather",
        description = "Returns today's weather forecast for the specified location",
        argsSpec = "<city or zip code>",
        argsExample = "Seattle, WA"
)
public class WeatherAction extends SimpleAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(WeatherAction.class);

    private WebTarget target;

    public WeatherAction() {
        Client client = ClientBuilder.newClient(new ClientConfig());
        target = client.target("https://query.yahooapis.com/v1/public/yql");
    }

    @Override
    public SlackerOutput execute(SlackerContext ctx) throws SlackerException {
        try {
            if (ctx.getRequestArgs() == null || ctx.getRequestArgs().length == 0) {
                throw new NoArgumentsFoundException("Location info argument is required");
            }
            final String input = ctx.getRequestArgs()[0].replaceAll("[\"|,]", "");
            String yql = String.format("select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"%s\")",
                    input);
            logger.debug(yql);

            String json = target.queryParam("q", yql).queryParam("format", "json")
            .queryParam("env", "store%3A%2F%2Fdatatables.org%2Falltableswithkeys")
            .request().get(String.class);

            logger.debug("Response: {}", json);
            ObjectMapper mapper = new ObjectMapper();
            YahooResponse<WeatherResults> response = mapper.readValue(json, new TypeReference<YahooResponse<WeatherResults>>() {});

            WeatherResults results = response.getResults();
            return new TextOutput(results != null ? results.toString() : "Sorry, cannot retrieve weather for: " + input );
        } catch (IOException e) {
            logger.warn("Error while trying to retrieve weather for: {}", ctx.getRequestArgs(), e);
            throw new SlackerException("Error while retrieving weather info", e);
        }
    }

}
