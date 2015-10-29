package com.labs2160.slacker.plugin.extra;

import java.util.Properties;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.annotation.ActionDescription;

@ActionDescription(
        name = "Broken Record",
        description = "Responds the with the same configured message all the time"
)
public class StaticResponseAction implements Action {

    private String response = "<no response configured>";

    @Override
    public void setConfiguration(Properties config) {
        response = config.getProperty("response");
    }

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        ctx.setResponseMessage(response);
        return true;
    }
}
