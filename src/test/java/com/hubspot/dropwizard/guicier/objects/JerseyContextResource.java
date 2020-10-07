package com.hubspot.dropwizard.guicier.objects;

import com.google.inject.*;

import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/jersey-context")
@Produces(APPLICATION_JSON)
public class JerseyContextResource {
    private final Injector injector;

    @Inject
    public JerseyContextResource(Injector injector) {
        this.injector = injector;
    }

    @GET
    @Path("/is-resolvable-by-guice")
    public boolean isResolvableByGuice(@QueryParam("className") String className) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(className);
        try {
            return injector.getInstance(clazz) != null;
        } catch (ConfigurationException e) {
            return false;
        }
    }
}
