package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.SimpleAbstractAction;
import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.annotation.ActionDescription;
import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;

@ActionDescription(
        name = "Echo",
        description = "Simply echoes back any arguments passed in",
        argsSpec = "<string to echo back>",
        argsExample = "\"hello world\""
)
public class EchoAction extends SimpleAbstractAction {

    @Override
    public SlackerOutput execute(SlackerContext ctx) throws SlackerException {
        String [] args = ctx.getRequestArgs();
        if (args.length == 0) {
            return new TextOutput("... echo!!");
        } else {
            StringBuilder sb = new StringBuilder("...");
            for (int i = 0; i < args.length; i++) {
                sb.append(" ");
                sb.append(args[i]);
            }
            return new TextOutput(sb.toString());
        }
    }
}
