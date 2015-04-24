package com.labs2160.slacker.plugin.rs;

import java.io.IOException;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.Response;

public class CollectorServlet extends HttpServlet {
    public static final String QUERY_STRING_PARAM = "request";

    /** serial uid */
    private static final long serialVersionUID = 1668136348573520630L;

    private final static Logger logger = LoggerFactory.getLogger(CollectorServlet.class);

    private RequestHandler handler;

    public CollectorServlet(RequestHandler handler) {
        this.handler = handler;
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String requestString = req.getParameter(QUERY_STRING_PARAM);
        try {
            resp.setContentType("text/plain");
            resp.setContentType("UTF-8");

            if (requestString == null || requestString.trim().length() == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().append("\"" + QUERY_STRING_PARAM + "\" parameter is required");
            } else {
                Request request = new Request("REST API", requestString.split(" "));
                Future<Response> future = handler.handle(request);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().append(future.get().getMessage());
            }
        } catch (InvalidRequestException e) {
            logger.debug("Bad request: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().append(e.getMessage());
        } catch (Exception e) {
            final String msg = "Error processing request string: " + requestString;
            logger.warn(msg, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append(msg);
        }
    }
}
