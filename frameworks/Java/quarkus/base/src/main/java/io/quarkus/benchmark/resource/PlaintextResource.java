package io.quarkus.benchmark.resource;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;

import javax.inject.Singleton;

@Singleton
public class PlaintextResource {
    private static final String HELLO = "Hello, World!";

    @Route(path = "/plaintext", methods = HttpMethod.GET)
    public void plaintext(final RoutingExchange exchange) {
        exchange.ok().end(HELLO);
    }
}
