package io.quarkus.benchmark.resource.pgclient;

import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.resource.BaseResource;
import io.quarkus.vertx.web.Route;
import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgIterator;
import io.reactiverse.pgclient.PgPoolOptions;
import io.reactiverse.pgclient.Row;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.mutiny.pgclient.PgPool;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;

//DB pool size 4
//wrk -H 'Host: tfb-server' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 15 -c 128 --timeout 8 -t 4 "http://localhost:8080/pgclient/reactiverseCustomPool/db"
//        Running 15s test @ http://localhost:8080/pgclient/reactiverseCustomPool/db
//        4 threads and 128 connections
//        Thread Stats   Avg      Stdev     Max   +/- Stdev
//        Latency     8.35ms    7.93ms 141.84ms   85.64%
//        Req/Sec     4.56k     1.04k    7.25k    67.83%
//        Latency Distribution
//        50%    6.21ms
//        75%   11.20ms
//        90%   17.98ms
//        99%   37.65ms
//        272964 requests in 15.10s, 41.07MB read
//        Requests/sec:  18077.89
//        Transfer/sec:      2.72MB


@Singleton
public class DbReactiverseCustomPoolResource extends BaseResource {

    private static final String UPDATE_WORLD = "UPDATE world SET randomnumber=$1 WHERE id=$2";
    private static final String SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";
    private static final String SELECT_FORTUNE = "SELECT id, message from FORTUNE";

    private static final String SERVER_ERROR = "Error Occurred: ";
    private static final String BAD_REQUEST = "Bad Request";


    @Inject
    PgClients pgClients;


    @Route(path = "/pgclient/db", methods = HttpMethod.GET)
    public final void dbHandler(final RoutingContext ctx) {
        pgClients.getOne().preparedQuery(SELECT_WORLD, io.reactiverse.pgclient.Tuple.of(randomWorldNumber()), res -> {
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
