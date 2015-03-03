package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.api.WorkflowException;

public interface WorkflowEngine extends RequestHandler {

	void start();
	
	/**
	 * TODO: change to Future<WorkflowContext>, add threadmanager
	 * @param key
	 * @param args
	 * @return
	 * @throws InvalidRequestException
	 * @throws WorkflowException
	 */
	WorkflowContext handle(Request request) throws InvalidRequestException, WorkflowException;
	
	void shutdown();
}
