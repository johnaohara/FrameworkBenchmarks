package io.quarkus.benchmark.resource.hibernate.reactive;

import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/plaintext")
public class PlaintextResource {
    private static final String HELLO = "Hello, World!";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String plaintext() {
        return HELLO;
    }

    @GET
    @Path("/uni")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> plaintextUni() {
        return Uni.createFrom().item(HELLO);
    }


}
