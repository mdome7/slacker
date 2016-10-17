package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.annotation.ActionDescription;
import com.labs2160.slacker.api.annotation.ConfigParam;

import java.util.Map;
import java.util.Properties;

@ActionDescription(
        name = "Broken Record",
        description = "Responds the with the same configured message all the time",
        configParams = {
            @ConfigParam(key = "response", description = "the static text to respond with", example = "I am busy right now")
        }
)
public class StaticResponseAction implements Action {

    private String response = "<no response configured>";

    @Override
    public void setComponents(Map<String, Resource> resources, Properties config) throws InitializationException {
        response = config.getProperty("response");
    }

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        ctx.setResponseMessage(response);
        return true;
    }
}
