package com.labs2160.slacker.api;

public interface Endpoint extends Configurable {

	/**
	 * Deliver the action's response to the endpoint
	 * @param response
	 * @return true if the response is sent successfully, false otherwise
	 * @throws SlackerException
	 */
	boolean deliverResponse(SlackerResponse response) throws SlackerException;

}
