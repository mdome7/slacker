package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.Resource;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.SlackerResponse;

import java.util.Map;
import java.util.Properties;

/**
 * Used for debugging
 */
public class PrintToConsoleEndpoint implements Endpoint {

    @Override
    public void setConfiguration(Map<String, Resource> resources, Properties config) {
        // do nothing
    }

    @Override
    public boolean deliverResponse(SlackerResponse response) throws SlackerException {
        System.out.println(response.getMessage());
        return true;
    }
}
