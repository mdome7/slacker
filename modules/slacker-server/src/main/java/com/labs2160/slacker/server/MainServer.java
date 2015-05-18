package com.labs2160.slacker.server;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.jboss.weld.environment.se.Weld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.server.rs.AdminResource;

public class MainServer {

    private static final Logger logger = LoggerFactory.getLogger(MainServer.class);

    /** temp */
    public static final String [][] TEMP_CREDENTIALS = {{"user1", "user1"}, {"user2", "user2"}, {"user3", "user3"}};

    public static final String CONTEXT_PATH = "/";

    public static final int SERVER_PORT = 7080;

	public static void main(String[] args) throws Exception {
		Server server = null;
        Weld weld = new Weld();
		try {
	        weld.initialize();

	        server = startServer(args);
			server.join();
		} finally {
			if (server != null) {
				server.destroy();
			}
			weld.shutdown();
		}
	}

	public static Server startServer(String ... args) throws Exception {
		ServletHolder jerseyServlet = new ServletHolder(ServletContainer.class);
		jerseyServlet.setInitOrder(0);

        final String scanPackages = AdminResource.class.getPackage().getName();
        logger.info("Scanning for REST components under packages: {}", scanPackages);
        jerseyServlet.setInitParameter(ServerProperties.PROVIDER_PACKAGES, scanPackages);//Set the package where the services reside

		Server server = new Server(SERVER_PORT);
        ServletContextHandler sch = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
		sch.addServlet(jerseyServlet, CONTEXT_PATH + "*");
		sch.setSecurityHandler(createBasicAuthHandler());

		server.setHandler(sch);
		server.start();
		return server;
	}


	/**
	 * There are a lot of different ways you can setup a SecurityHandler for Jetty.
	 * For now, we just hard-code the credentials here.
	 *
	 * Eventually, we could potentially get the hashed credentials from the Metadata server
	 * (which should contain information about Providers) and cache locally.
	 *
	 * @return
	 */
	private static final SecurityHandler createBasicAuthHandler() {
		final String defaultRealm = "secureRealm";
		final String [] defaultRoles = { "user" };
		HashLoginService l = new HashLoginService();
		for (String [] userPassword : TEMP_CREDENTIALS) {
			l.putUser(userPassword[0], Credential.getCredential(userPassword[1]), defaultRoles);
		}
		l.setName(defaultRealm);

		Constraint constraint = new Constraint();
		constraint.setName(Constraint.__BASIC_AUTH);
		constraint.setRoles(defaultRoles);
		constraint.setAuthenticate(true);

		ConstraintMapping cm = new ConstraintMapping();
		cm.setConstraint(constraint);
		cm.setPathSpec("/*");

		ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
		csh.setAuthenticator(new BasicAuthenticator());
		csh.setRealmName(defaultRealm);
		csh.addConstraintMapping(cm);
		csh.setLoginService(l);

		return csh;
	}
}
