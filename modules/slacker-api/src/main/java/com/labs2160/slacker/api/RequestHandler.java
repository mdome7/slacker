package com.labs2160.slacker.api;

import java.util.concurrent.Future;

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
	Future<Response> handle(Request request) throws InvalidRequestException, NoArgumentsFoundException, SlackerException;
}
