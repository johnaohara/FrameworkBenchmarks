package io.quarkus.benchmark.resource.pgclient;

import java.util.Collections;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class JsonResource extends BaseResource {

    private static final String MESSAGE = "message";
    private static final String HELLO = "Hello, World!";
    private static final Map<String, String> map = Collections.singletonMap( MESSAGE, HELLO );

    @Route(path = "json")
    public void json(RoutingContext rc) {
        sendJson(rc, map);
    }
}

