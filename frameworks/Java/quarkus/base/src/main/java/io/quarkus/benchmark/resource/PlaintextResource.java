package io.quarkus.benchmark.resource;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;
import org.apache.http.HttpHeaders;

import javax.inject.Singleton;

@Singleton
public class PlaintextResource extends BaseResource {
    private static final String HELLO = "Hello, World!";

    @Route(path = "/plaintext", methods = HttpMethod.GET)
    public void plaintext(final RoutingExchange exchange) {
        exchange.ok()
                .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .putHeader(HttpHeaders.SERVER, SERVER)
                .putHeader(HttpHeaders.DATE, date)
                .end(HELLO);
    }
}
