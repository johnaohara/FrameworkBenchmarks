package io.quarkus.benchmark.resource.pgclient;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class PlaintextResource {
    private static final String HELLO = "Hello, World!";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String PLAIN_TEXT = "text/plain";

    @Route(path = "plaintext")
    public void plaintext(RoutingContext rc) {
        rc.response().putHeader( CONTENT_TYPE , PLAIN_TEXT);
        rc.response().end(HELLO);
    }
}
