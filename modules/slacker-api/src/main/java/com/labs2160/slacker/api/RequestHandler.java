package com.labs2160.slacker.api;

public interface RequestHandler {

	SlackerContext handle(Request request) throws InvalidRequestException, NoArgumentsFoundException, SlackerException;
}
