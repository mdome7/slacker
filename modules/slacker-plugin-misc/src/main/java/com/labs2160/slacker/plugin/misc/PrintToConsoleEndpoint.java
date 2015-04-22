package com.labs2160.slacker.plugin.misc;

import java.util.Map;

import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.Response;
import com.labs2160.slacker.api.SlackerException;

/**
 * Used for debugging
 */
public class PrintToConsoleEndpoint implements Endpoint {

    @Override
    public void setConfiguration(Map<String, ?> config) {
        // do nothing
    }

    @Override
    public boolean deliverResponse(Response response) throws SlackerException {
        System.out.println(response.getMessage());
        return true;
    }
}
