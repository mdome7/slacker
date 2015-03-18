package com.labs2160.slacker.api;

public interface Action {

	/**
	 * Execute the action
	 * @param ctx
	 * @return true if the workflow should continue normally, false if error (check ctx.getException())
	 * @throws SlackerException
	 */
	boolean execute(SlackerContext ctx) throws SlackerException;
}
