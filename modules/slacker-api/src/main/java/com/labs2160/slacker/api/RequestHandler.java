package com.labs2160.slacker.api;

import com.labs2160.slacker.api.response.SlackerOutput;

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
	Future<SlackerOutput> handle(SlackerRequest request) throws InvalidRequestException, NoArgumentsFoundException, SlackerException;
}
