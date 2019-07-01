package io.quarkus.benchmark.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class ServerHeaderFilter implements ContainerResponseFilter {

    private static final String SERVER_HEADER = "Server";
private static final String SERVER = "Quarkus";

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        responseContext.getHeaders().add(SERVER_HEADER, SERVER);
    }
}