package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.Resource;
import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.annotation.ActionDescription;

import java.util.Map;
import java.util.Properties;

@ActionDescription(
        name = "Echo",
        description = "Simply echoes back any arguments passed in",
        argsSpec = "<string to echo back>",
        argsExample = "\"hello world\""
)
public class EchoAction implements Action {

    @Override
    public void setComponents(Map<String, Resource> resources, Properties config) {
        // do nothing
    }

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        String [] args = ctx.getRequestArgs();
        if (args.length == 0) {
            ctx.setResponseMessage("... echo!!");
        } else {
            StringBuilder sb = new StringBuilder("...");
            for (int i = 0; i < args.length; i++) {
                sb.append(" ");
                sb.append(args[i]);
            }
            ctx.setResponseMessage(sb.toString());
        }
        return true;
    }
}
