package com.labs2160.slacker.api;

public interface Endpoint extends Configurable {

	public boolean deliverResponse(SlackerResponse response) throws SlackerException;

}
