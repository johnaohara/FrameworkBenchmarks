package io.quarkus.benchmark.resource.pgclient;

import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.resource.BaseResource;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgIterator;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.pgclient.Row;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.Tuple;
import org.apache.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

//DB pool size 4
//wrk -H 'Host: tfb-server' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 15 -c 128 --timeout 8 -t 4 "http://localhost:8080/pgclient/reactiverse/db"
//        Running 15s test @ http://localhost:8080/pgclient/reactiverse/db
//        4 threads and 128 connections
//        Thread Stats   Avg      Stdev     Max   +/- Stdev
//        Latency     5.21ms    3.19ms  83.02ms   89.69%
//        Req/Sec     6.52k     0.90k    8.78k    70.00%
//        Latency Distribution
//        50%    4.36ms
//        75%    4.72ms
//        90%    7.97ms
//        99%   19.20ms
//        389815 requests in 15.05s, 58.66MB read
//        Requests/sec:  25907.22
//        Transfer/sec:      3.90MB


@Singleton
public class DbReactiverseResource extends BaseResource {

    private static final String SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";


    PgClient client;

    @Inject
    public DbReactiverseResource(Vertx vertx) {
        PgPoolOptions options = new PgPoolOptions();
        options.setDatabase("hello_world");
        options.setHost("localhost");
        options.setPort(5432);
        options.setUser("benchmarkdbuser");
        options.setPassword("benchmarkdbpass");
        options.setCachePreparedStatements(true);
        client = PgClient.pool(vertx, new PgPoolOptions(options).setMaxSize(32));
    }



    @Route(path = "/pgclient/db", methods = HttpMethod.GET)
    public final void dbHandler(final RoutingContext ctx) {
        client.preparedQuery(SELECT_WORLD, io.reactiverse.pgclient.Tuple.of(randomWorldNumber()), res -> {
            if (res.succeeded()) {
                final PgIterator resultSet = res.result().iterator();
                if (!resultSet.hasNext()) {
                    ctx.response()
                            .setStatusCode(404)
                            .end();
                    return;
                }
                final Row row = resultSet.next();
                ctx.response()
                        .putHeader(HttpHeaders.SERVER, SERVER)
                        .putHeader(HttpHeaders.DATE, date)
                        .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .end(Json.encodeToBuffer(new World(row.getInteger(0), row.getInteger(1))));
            } else {
                ctx.fail(res.cause());
            }
        });
    }

     private int randomWorldNumber() {
        return 1 + ThreadLocalRandom.current().nextInt(10000);
    }

}
