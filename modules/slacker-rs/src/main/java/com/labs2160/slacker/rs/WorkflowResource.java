package com.labs2160.slacker.rs;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.core.ApplicationManager;
import com.labs2160.slacker.core.cdi.Eager;

@Path("workflows")
public class WorkflowResource {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkflowResource.class);

	@Inject
	@Eager
	private ApplicationManager app;
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@QueryParam("req") String req) {
    	try {
    		if (req == null || req.trim().length() == 0) {
        		return Response.status(Response.Status.BAD_REQUEST).entity("req parameter required").build();
    		} else {
    			String [] args = req.split(" ");
	    		WorkflowContext ctx = app.getWorkflowEngine().handle(new Request("REST API", args));
	    		return Response.ok(ctx.getResponseMessage()).build();
    		}
    	} catch (InvalidRequestException e) {
    		logger.warn("Bad request: {}", e.getMessage());
    		return Response.status(Response.Status.BAD_REQUEST).build();
    	} catch (Exception e) {
    		logger.error("Error while processing request; {}", e.getMessage(), e);
    		return Response.status(Response.Status.BAD_REQUEST).build();
		}
    }
}
