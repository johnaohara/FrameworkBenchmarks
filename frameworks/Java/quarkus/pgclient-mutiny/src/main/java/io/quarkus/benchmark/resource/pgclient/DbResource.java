package io.quarkus.benchmark.resource.pgclient;

import io.quarkus.benchmark.repository.pgclient.WorldRepository;
import io.quarkus.benchmark.resource.BaseResource;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import org.apache.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DbResource extends BaseResource {


    private static final String SERVER_ERROR = "Error Occurred: ";

    @Inject
    WorldRepository repository;


    @Route(path = "/pgclient/db", methods = HttpMethod.GET)
    public void db(final RoutingExchange routingExchange) {

        repository.findRandom().subscribe()
                .with(
                        world -> routingExchange.ok()
                                .putHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                                .putHeader(org.apache.http.HttpHeaders.SERVER, SERVER)
                                .putHeader(org.apache.http.HttpHeaders.DATE, date)
                                .end(JsonObject.mapFrom(world).toString()),

                        failure -> routingExchange.response()
                                .setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                                .putHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                                .putHeader(org.apache.http.HttpHeaders.SERVER, SERVER)
                                .putHeader(org.apache.http.HttpHeaders.DATE, date)
                                .end(SERVER_ERROR.concat(failure.getMessage()))
                );

    }





}
