package com.labs2160.slacker.api;

import com.labs2160.slacker.api.response.SlackerOutput;

public interface Endpoint <O extends SlackerOutput> extends WorkflowComponent {

	/**
	 * Deliver a workflow output to the endpoint
	 * @param output
	 * @return true if the response is sent successfully, false otherwise
	 * @throws SlackerException
	 */
	boolean deliverResponse(O output) throws SlackerException;

}
