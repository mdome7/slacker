package com.labs2160.slacker.api.misc;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;

public class EchoAction implements Action {

	@Override
	public boolean execute(WorkflowContext ctx) throws WorkflowException {
		String [] requestArgs = ctx.getWorkflowArgs();
		StringBuilder sb = new StringBuilder("Message:");
		for (int i = 0; i < requestArgs.length; i++) {
			sb.append(" ");
			sb.append(requestArgs[i]);
		}
		ctx.setOutputMessage(sb.toString());
		return true;
	}

}
