package io.quarkus.benchmark.resource;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import org.apache.http.HttpHeaders;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

@Singleton
public class PlaintextResource extends BaseResource {
    private static final String HELLO = "Hello, World!";
    private static final Buffer HELLO_WORLD_BUFFER = Buffer.factory.directBuffer(HELLO, "UTF-8");

    @Route(path = "/plaintext", methods = HttpMethod.GET)
    public void plaintext(final RoutingExchange exchange) {
        exchange.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .putHeader(HttpHeaders.SERVER, SERVER)
                .putHeader(HttpHeaders.DATE, date)
                .end(HELLO_WORLD_BUFFER);
    }
}
