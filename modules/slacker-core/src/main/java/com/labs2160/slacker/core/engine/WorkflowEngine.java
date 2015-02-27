package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.WorkflowException;

public interface WorkflowEngine {

	void start();
	
	void submitRequest(String key, String ... args) throws InvalidRequestException, WorkflowException;
	
	void shutdown();
}
