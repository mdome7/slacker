package com.labs2160.slacker.rs.admin;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.labs2160.slacker.core.cdi.Eager;
import com.labs2160.slacker.rs.ApplicationManager;
import com.labs2160.slacker.rs.ApplicationStatus;

@Path("admin")
public class AdminResource {

	@Inject
	@Eager
	private ApplicationManager app;

	private static int count;

	@GET
	@Path("status")
    @Produces(MediaType.APPLICATION_JSON)
	public ServerInfo get() {
		return new ServerInfo(app.getStatus(), app.getStartDate());
	}

    @PUT
	@Path("status")
    public Response update(@FormParam("newStatus") String newStatus) {
    	try {
    		app.setStatus(ApplicationStatus.valueOf(newStatus));
    		return Response.ok().build();
    	} catch (IllegalArgumentException e) {
    		return Response.status(Response.Status.BAD_REQUEST).build();
    	}
    }

	@Path("fakeStatus")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	public Response fake() {
		if (count % 10 == 0) {
			return Response.ok().build();
		}
		return Response.status(Response.Status.SERVICE_UNAVAILABLE).build();
	}

}
