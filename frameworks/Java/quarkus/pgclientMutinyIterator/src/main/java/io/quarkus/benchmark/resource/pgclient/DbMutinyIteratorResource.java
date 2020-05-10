package io.quarkus.benchmark.resource.pgclient;

import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.resource.BaseResource;
import io.quarkus.vertx.web.Route;
import io.quarkus.vertx.web.RoutingExchange;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowIterator;
import io.vertx.mutiny.sqlclient.Tuple;
import org.apache.http.HttpStatus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ThreadLocalRandom;


//DB pool size 4
//wrk -H 'Host: tfb-server' -H 'Accept: application/json,text/html;q=0.9,application/xhtml+xml;q=0.9,application/xml;q=0.8,*/*;q=0.7' -H 'Connection: keep-alive' --latency -d 15 -c 128 --timeout 8 -t 4 "http://localhost:8080/pgclient/mutinyv2/db"
//        Running 15s test @ http://localhost:8080/pgclient/mutinyv2/db
//        4 threads and 128 connections
//        Thread Stats   Avg      Stdev     Max   +/- Stdev
//        Latency     9.87ms    5.59ms  66.26ms   77.05%
//        Req/Sec     3.37k   572.94     9.33k    68.45%
//        Latency Distribution
//        50%    8.77ms
//        75%   12.24ms
//        90%   16.74ms
//        99%   29.02ms
//        201570 requests in 15.10s, 30.33MB read
//        Requests/sec:  13349.13
//        Transfer/sec:      2.01MB



@Singleton
public class DbMutinyIteratorResource extends BaseResource {

    private static final String UPDATE_WORLD = "UPDATE world SET randomnumber=$1 WHERE id=$2";
    private static final String SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";
    private static final String SELECT_FORTUNE = "SELECT id, message from FORTUNE";

    private static final String SERVER_ERROR = "Error Occurred: ";
    private static final String BAD_REQUEST = "Bad Request";

    @Inject
    PgPool pgPool;

    @Route(path = "/pgclient/db", methods = HttpMethod.GET)
    public void dbMutiny2(final RoutingExchange routingExchange) {

        pgPool.preparedQuery(SELECT_WORLD, Tuple.of(randomWorldNumber()))
                .subscribe().with(
                        rowSet -> {
                            final RowIterator rowRowIterator = rowSet.iterator();
                            if (!rowRowIterator.hasNext()) {
                                routingExchange.response()
                                        .setStatusCode(404)
                                        .end();
                                return;
                            }
                            Row row = (Row) rowRowIterator.next();
                            routingExchange.response()
                                    .putHeader(HttpHeaders.SERVER, SERVER)
                                    .putHeader(HttpHeaders.DATE, date)
                                    .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                                    .end(Json.encodeToBuffer(World.from(row)));
                        },
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
