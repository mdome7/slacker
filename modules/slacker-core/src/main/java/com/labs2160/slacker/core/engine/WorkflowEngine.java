package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerRequest;
import com.labs2160.slacker.core.event.WorkflowExecutionListener;

import java.util.concurrent.Future;

public interface WorkflowEngine extends RequestHandler {

	void start();

	WorkflowRegistry getRegistry();

	void shutdown();

	void addWorkflowExecutionListener(WorkflowExecutionListener listener);

	/**
	 * Internal interface that handles requests and returns execution metadata along with the SlackerOutput.
	 * @param request
	 * @return
	 */
	Future<ExecutionDetails> handleRequest(SlackerRequest request) throws SlackerException;
}
