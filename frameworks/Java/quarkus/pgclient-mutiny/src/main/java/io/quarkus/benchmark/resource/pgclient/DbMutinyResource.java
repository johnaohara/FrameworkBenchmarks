package io.quarkus.benchmark.resource.pgclient;

import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.resource.BaseResource;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.smallrye.mutiny.Uni;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import org.apache.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

//DB pool size 4
//wrk -H 'Host: tfb-server' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 15 -c 128 --timeout 8 -t 4 "http://localhost:8080/pgclient/mutiny/db"
//        Running 15s test @ http://localhost:8080/pgclient/mutiny/db
//        4 threads and 128 connections
//        Thread Stats   Avg      Stdev     Max   +/- Stdev
//        Latency    10.43ms    5.96ms  89.22ms   79.46%
//        Req/Sec     3.20k   488.84     4.22k    70.83%
//        Latency Distribution
//        50%    9.31ms
//        75%   12.81ms
//        90%   17.22ms
//        99%   30.46ms
//        191220 requests in 15.08s, 28.77MB read
//        Requests/sec:  12679.10
//        Transfer/sec:      1.91MB


@Singleton
public class DbMutinyResource extends BaseResource {

    private static final String SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";

    private static final String SERVER_ERROR = "Error Occurred: ";

    @Inject
    PgPool pgPool;


    @Route(path = "/pgclient/db", methods = HttpMethod.GET)
    public void db(final RoutingExchange routingExchange) {

        pgPool.preparedQuery(SELECT_WORLD, Tuple.of(randomWorldNumber()))
                .map(rows -> rows.iterator().next())
                .onItem().produceUni(row -> Uni.createFrom().item(World.from(row))).subscribe()
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


    private int randomWorldNumber() {
        return 1 + ThreadLocalRandom.current().nextInt(10000);
    }


}
