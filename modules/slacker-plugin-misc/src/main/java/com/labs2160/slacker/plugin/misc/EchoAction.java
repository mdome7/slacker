package com.labs2160.slacker.plugin.misc;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;

public class EchoAction implements Action {

	@Override
	public boolean execute(WorkflowContext ctx) throws WorkflowException {
		String [] args = ctx.getWorkflowArgs();
		StringBuilder sb = new StringBuilder("Message:");
		for (int i = 0; i < args.length; i++) {
			sb.append(" ");
			sb.append(args[i]);
		}
		ctx.setResponseMessage(sb.toString());
		return true;
	}

}
