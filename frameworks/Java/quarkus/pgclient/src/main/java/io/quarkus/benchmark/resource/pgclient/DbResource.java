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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Singleton
public class DbResource extends BaseResource {

    private static final String UPDATE_WORLD = "UPDATE world SET randomnumber=$1 WHERE id=$2";
    private static final String SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";
    private static final String SELECT_FORTUNE = "SELECT id, message from FORTUNE";

    private static final String SERVER_ERROR = "Error Occurred: ";
    private static final String BAD_REQUEST = "Bad Request";

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


    @Route(path = "/pgclient/queries", methods = HttpMethod.GET)
    public void queries(final RoutingExchange routingExchange) {

        Optional<String> queries = routingExchange.getParam("queries");
        if (queries.isPresent()) {

            final int count = parseQueryCount(queries.get());

            routingExchange.ok()
                    .putHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                    .putHeader(org.apache.http.HttpHeaders.SERVER, SERVER)
                    .putHeader(org.apache.http.HttpHeaders.DATE, date)
                    .end(JsonObject.mapFrom(JsonObject.mapFrom(randomWorldForRead(count))).toString());

        } else { //Missing queries param
            routingExchange.response()
                    .setStatusCode(HttpStatus.SC_BAD_REQUEST)
                    .putHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                    .putHeader(org.apache.http.HttpHeaders.SERVER, SERVER)
                    .putHeader(org.apache.http.HttpHeaders.DATE, date)
                    .end(BAD_REQUEST);
        }

    }

    //
//    @GET
//    @Path("/updates")
    @Route(path = "/pgclient/updates", methods = HttpMethod.GET)
    public void updates(final RoutingExchange routingExchange) {

        Optional<String> query = routingExchange.getParam("query");
        if (query.isPresent()) {

            final int count = parseQueryCount(query.get());

            routingExchange.ok()
                    .putHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                    .putHeader(org.apache.http.HttpHeaders.SERVER, SERVER)
                    .putHeader(org.apache.http.HttpHeaders.DATE, date)
                    .end(JsonObject.mapFrom(JsonObject.mapFrom(randomWorldForRead(count))).toString());

        } else { //Missing queries param
            routingExchange.response()
                    .setStatusCode(HttpStatus.SC_BAD_REQUEST)
                    .putHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                    .putHeader(org.apache.http.HttpHeaders.SERVER, SERVER)
                    .putHeader(org.apache.http.HttpHeaders.DATE, date)
                    .end(BAD_REQUEST);
        }
    }
//    public CompletionStage<List<World>> updates(@QueryParam("queries") String queries) {
//        Single<World>[] worlds = new Single[parseQueryCount(queries)];
//        Arrays.setAll(worlds, i -> randomWorld().flatMapSingle(world -> {
//            world.setId(randomWorldNumber());
//            return worldRepository.update(world);
//        }));
//
//        return Single.concatArray(worlds)
//                .toList()
//                .to(m -> {
//                    CompletableFuture<List<World>> cf = new CompletableFuture<>();
//                    m.subscribe(cf::complete, cf::completeExceptionally);
//                    return cf;
//                });
//    }

    private int randomWorldNumber() {
        return 1 + ThreadLocalRandom.current().nextInt(10000);
    }

    private int parseQueryCount(String textValue) {
        if (textValue == null) {
            return 1;
        }
        int parsedValue;
        try {
            parsedValue = Integer.parseInt(textValue);
        } catch (NumberFormatException e) {
            return 1;
        }
        return Math.min(500, Math.max(1, parsedValue));
    }


    private World[] randomWorldForRead(int count) {

        //TODO:: validate speed of collection operations
        List<Integer> ids = new ArrayList<>(count);
        int counter = 0;
        while (counter < count) {
            counter += ids.add(randomWorldNumber()) ? 1 : 0; //Make sure each random world is unique
        }

        final World[] worlds = new World[count];

//        for (int i = 0; i < count; i++) {
//
//            int finalI = i;
//            fetchWorld(ids.get(i)).subscribe().with(
//                    world -> worlds[finalI] = world,
//                    failure -> failure.printStackTrace()
//            );
//
//        }
//
//        Uni<String> stringUni = pgPool.begin().flatMap(
//                transaction -> {
//                    Uni<RowSet<Row>> rowSetUni = transaction.query("SELECT 1");
//                    ids.iterator().forEachRemaining(idTuple -> rowSetUni.and(transaction.preparedQuery(SELECT_WORLD, Tuple.of(idTuple))));
//                    rowSetUni.onItem().produceUni(rows -> {
//                        transaction.commitAndForget();
//                        return Uni.createFrom().item("Test");
//                    });



//        Uni<Transaction> transactionUni = pgPool.begin();
//
//        transactionUni.flatMap( tx -> {
//            ids.iterator().forEachRemaining( idTuple -> tx.preparedQuery(SELECT_WORLD, idTuple) );
//
//        });
//
//
//        pgPool.preparedBatch(SELECT_WORLD, ids).subscribe().with(
//                rows -> rows.iterator().forEachRemaining( row -> worlds.add(World.from(row))),
//                failure -> failure.printStackTrace()
//        );

        return worlds;
    }

    private Uni<World> fetchWorld(int id) {
        return pgPool.preparedQuery(SELECT_WORLD, Tuple.of(id))
                .map(rows -> rows.iterator().next())
                .onItem().produceUni(row -> Uni.createFrom().item(World.from(row)));
    }

}
