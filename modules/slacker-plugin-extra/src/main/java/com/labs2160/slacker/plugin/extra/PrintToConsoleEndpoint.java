package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.Endpoint;
import com.labs2160.slacker.api.Resource;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;

import java.util.Map;
import java.util.Properties;

/**
 * Used for debugging
 */
public class PrintToConsoleEndpoint implements Endpoint<TextOutput> {

    @Override
    public void setComponents(Map<String, Resource> resources, Properties config) {
        // do nothing
    }

    @Override
    public boolean deliverResponse(TextOutput output) throws SlackerException {
        System.out.println(output.getMessage());
        return true;
    }
}
