package com.labs2160.slacker.plugin.rs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.response.BinaryDataOutput;
import com.labs2160.slacker.api.response.TextOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.response.SlackerOutput;

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

            if (requestString == null || requestString.trim().length() == 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().append("\"" + QUERY_STRING_PARAM + "\" parameter is required");
            } else {
                SlackerRequest request = new SlackerRequest("REST API", requestString.split(" "));
                try {
                    Future<SlackerOutput> future = handler.handle(request);
                    resp.setStatus(HttpServletResponse.SC_OK);

                    SlackerOutput output = future.get();

                    if (output instanceof TextOutput) {
                        final TextOutput to = (TextOutput) output;
                        resp.setCharacterEncoding("UTF-8");
                        resp.setContentType(to.getMediaType());
                        resp.getWriter().append(to.getMessage());
                    } else if (output instanceof BinaryDataOutput) {
                        final BinaryDataOutput bdo = (BinaryDataOutput) output;
                        resp.setContentType(bdo.getMediaType());
                        pipe(bdo.getInputStream(), resp.getOutputStream());
                    } else {
                        throw new IllegalStateException("Output of type " + output.getClass().getName() + " is not yet supported");
                    }
                } catch (ExecutionException ee) { // ExecutionException is just a wrapper
                    throw ee.getCause() != null ? (Exception) ee.getCause() : ee;
                }
            }
        } catch (InvalidRequestException | NoArgumentsFoundException e) {
            logger.debug("Bad request: {}", e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().append(e.getMessage());
        } catch (Exception e) {
            final String msg = "Error processing request string: " + requestString;
            logger.error(msg, e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().append(msg);
        }
    }

    private static long pipe(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[4096];
        long count = 0L;
        int n;
        while ((n = input.read(buffer)) != -1) {
            output.write(buffer, 0, n);
            count += (long) n;
        }
        return count;
    }
}
