package com.labs2160.slacker.plugin.extra;

import java.util.Properties;

import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.SlackerResponse;
import com.labs2160.slacker.api.SlackerException;

/**
 * Used for debugging
 */
public class PrintToConsoleEndpoint implements Endpoint {

    @Override
    public void setConfiguration(Properties config) {
        // do nothing
    }

    @Override
    public boolean deliverResponse(SlackerResponse response) throws SlackerException {
        System.out.println(response.getMessage());
        return true;
    }
}
