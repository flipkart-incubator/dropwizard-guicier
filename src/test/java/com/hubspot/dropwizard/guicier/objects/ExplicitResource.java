package com.hubspot.dropwizard.guicier.objects;

import com.google.inject.Inject;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/explicit")
@Produces(APPLICATION_JSON)
public class ExplicitResource {
    private final ExplicitDAO dao;

    @Inject
    public ExplicitResource(ExplicitDAO dao) {
        this.dao = dao;
    }

    @GET
    @Path("/message")
    public String getMessage() {
        return dao.getMessage();
    }

    public ExplicitDAO getDAO() {
        return dao;
    }

}
