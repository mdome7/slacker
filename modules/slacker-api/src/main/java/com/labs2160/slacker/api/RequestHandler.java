package com.labs2160.slacker.api;

public interface RequestHandler {

    /**
     *
     * @param request
     * @return
     * @throws InvalidRequestException
     * @throws NoArgumentsFoundException
     * @throws SlackerException
     *
     * TODO: return Response instead of SlackerContext
     */
	SlackerContext handle(Request request) throws InvalidRequestException, NoArgumentsFoundException, SlackerException;
}
