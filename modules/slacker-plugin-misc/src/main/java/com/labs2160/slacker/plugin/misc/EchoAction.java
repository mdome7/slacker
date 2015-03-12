package com.labs2160.slacker.plugin.misc;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.WorkflowContext;

public class EchoAction implements Action {

	@Override
	public boolean execute(WorkflowContext ctx) throws SlackerException {
		String [] args = ctx.getWorkflowArgs();
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
