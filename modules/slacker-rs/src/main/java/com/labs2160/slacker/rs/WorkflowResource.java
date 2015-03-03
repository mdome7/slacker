package com.labs2160.slacker.rs;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.WorkflowContext;
import com.labs2160.slacker.core.ApplicationManager;
import com.labs2160.slacker.core.cdi.Eager;

@Path("workflows")
public class WorkflowResource {

	@Inject
	@Eager
	private ApplicationManager app;

    @POST
	@Path("{key}")
    public Response submit(@PathParam("key") String key, @FormParam("args") String args) {
    	try {
    		WorkflowContext ctx = app.getWorkflowEngine().handle(new Request(key, args));
    		return Response.ok(ctx.getOutputMessage()).build();
    	} catch (InvalidRequestException e) {
    		return Response.status(Response.Status.BAD_REQUEST).build();
    	} catch (Exception e) {
    		return Response.status(Response.Status.BAD_REQUEST).build();
		}
    }
}
