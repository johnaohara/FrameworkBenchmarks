package io.quarkus.benchmark.resource;

import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import org.apache.http.HttpHeaders;

import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class JsonResource extends BaseResource {
    private static final String MESSAGE = "message";
    private static final String HELLO = "Hello, World!";

    @Route(path = "/json", methods = HttpMethod.GET)
    public void json(final RoutingExchange exchange) {

        exchange.ok()
                .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .putHeader(HttpHeaders.SERVER, SERVER)
                .putHeader(HttpHeaders.DATE, date)
                .end(
                        JsonObject
                                .mapFrom(Map.of(MESSAGE, HELLO))
                                .toString()
                );

    }
}
