package com.labs2160.slacker.api;

public interface Trigger extends Configurable {

	/**
	 * Start the trigger
	 * @param handler
	 */
	void start(RequestHandler handler);

}
