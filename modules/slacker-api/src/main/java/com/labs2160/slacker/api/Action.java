package com.labs2160.slacker.api;

import com.labs2160.slacker.api.response.SlackerOutput;

public interface Action extends WorkflowComponent {

	/**
	 * Execute the action
	 * @param ctx
	 * @return true if the workflow should continue normally, false if error (check ctx.getException())
	 * @throws SlackerException
	 */
	SlackerOutput execute(SlackerContext ctx) throws SlackerException;
}
