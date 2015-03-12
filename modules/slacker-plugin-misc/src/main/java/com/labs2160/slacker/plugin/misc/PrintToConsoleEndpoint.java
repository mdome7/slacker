package com.labs2160.slacker.plugin.misc;

import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.SlackerException;

/**
 * Used for debugging
 */
public class PrintToConsoleEndpoint implements Endpoint {
	
	@Override
	public boolean execute(WorkflowContext ctx) throws SlackerException {
		System.out.println(ctx.getResponseMessage());
		return true;
	}

}
