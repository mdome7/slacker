package com.labs2160.slacker.api;

public interface Endpoint {

	public boolean deliverResponse(Response response) throws SlackerException;

}
