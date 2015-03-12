package com.labs2160.slacker.core.engine;

import com.labs2160.slacker.api.RequestHandler;

public interface WorkflowEngine extends RequestHandler {

	void start();
	
	WorkflowRegistry getRegistry();
	
	void shutdown();
}
