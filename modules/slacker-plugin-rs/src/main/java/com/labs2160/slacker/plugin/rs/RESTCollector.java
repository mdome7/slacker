package com.labs2160.slacker.plugin.rs;

import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.Resource;
import com.labs2160.slacker.api.SchedulerTask;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Collector that exposes a REST endpoint for requests.
 * The input string is passed as a query string parameter "request".
 * For example:
 * <p>(GET) http://<i>host:port</i>?request=echo%20test</p>
 *
 * Collector configuration parameters:<br/>
 * <dl>
 *   <dt>port</dt>
 *   <dd>(optional) port number where the REST endpoint will listen on (default: 7777)</dd>
 * </dl>
 *
 */
public class RESTCollector implements RequestCollector {
    public static final String CONFIG_PORT = "port";

    public static final int DEFAULT_PORT = 7777;

    private static final Logger logger = LoggerFactory.getLogger(RESTCollector.class);

    private Server server;

    private int port;

    public RESTCollector() {
        this(DEFAULT_PORT);
    }

    public RESTCollector(int port) {
        this.port = port;
    }

    @Override
    public void start(RequestHandler handler) {
        logger.debug("Starting servlet");
        CollectorServlet servlet = new CollectorServlet(handler);
        ServletHolder holder = new ServletHolder(servlet);
        holder.setInitOrder(0);

        server = new Server(port);
        ServletContextHandler sch = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        sch.addServlet(holder, "/*");

        server.setHandler(sch);
        try {
            server.start();
            logger.info("Listening on port {}", port);
        } catch (Exception e) {
            throw new RuntimeException("Failed to start " + this.getClass().getSimpleName(), e);
        }
    }

    @Override
    public void shutdown() {
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                throw new RuntimeException("Failed to stop " + this.getClass().getSimpleName(), e);
            }
        }
    }

    @Override
    public boolean isActive() {
        return server != null && server.isRunning();
    }

    @Override
    public SchedulerTask[] getSchedulerTasks() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setConfiguration(Map<String, Resource> resources, Properties config) {
        String portString = config.getProperty(CONFIG_PORT, "" + DEFAULT_PORT);
        this.port = Integer.parseInt(portString);
    }

}
