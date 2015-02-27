package com.labs2160.slacker.api.misc;

import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;

public class PrintToConsoleEndpoint implements Endpoint {
	
	@Override
	public boolean execute(WorkflowContext ctx) throws WorkflowException {
		System.out.println(ctx.getOutputMessage());
		return true;
	}

}
