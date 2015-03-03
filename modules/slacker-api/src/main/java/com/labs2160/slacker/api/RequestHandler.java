package com.labs2160.slacker.api;

public interface RequestHandler {

	WorkflowContext handle(Request request) throws InvalidRequestException, SlackerException;
}
