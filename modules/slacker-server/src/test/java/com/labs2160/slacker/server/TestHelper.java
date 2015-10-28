package com.labs2160.slacker.server;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.labs2160.slacker.server.MainServer;

/**
 * General helper class for testing.
 */
public final class TestHelper {

	private TestHelper() {}

	public static WebTarget prepareWebTarget(Server server) {
		Client client = ClientBuilder.newClient(new ClientConfig());
		client.register(prepareHttpAuthenticationFeature());
		return client.target(server.getURI()).path(MainServer.CONTEXT_PATH);
	}

	public static WebTarget prepareWebTargetNoCredentials(Server server) {
		Client client = ClientBuilder.newClient(new ClientConfig());
		return client.target(server.getURI()).path(MainServer.CONTEXT_PATH);
	}

	/**
	 * Central place to make sure the right credentials are supplied for tests.
	 * Update this depending on how Server authentication is setup for testing.
	 * @return
	 */
	public static HttpAuthenticationFeature prepareHttpAuthenticationFeature() {
		return HttpAuthenticationFeature.basic(MainServer.TEMP_CREDENTIALS[0][0], MainServer.TEMP_CREDENTIALS[0][1]);
	}

}
