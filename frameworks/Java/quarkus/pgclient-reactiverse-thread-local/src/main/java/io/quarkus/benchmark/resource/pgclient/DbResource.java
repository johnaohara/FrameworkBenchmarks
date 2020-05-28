package io.quarkus.benchmark.resource.pgclient;

import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.repository.pgclient.WorldRepository;
import io.quarkus.benchmark.resource.BaseResource;
import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;


@ApplicationScoped
public class DbResource  extends BaseResource {

    @Inject
    WorldRepository worldRepository;

    @Route(path = "/db", methods = HttpMethod.GET)
    public void db(final RoutingContext ctx) {
        randomWorld().thenAccept( world -> {
            ctx.response()
                    .putHeader(HttpHeaders.SERVER, SERVER)
                    .putHeader(HttpHeaders.DATE, date)
                    .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodeToBuffer(world));
        }).exceptionally( err -> {
            ctx.response()
                    .setStatusCode(404)
                    .end();
            return null;
        });
    }

//    @GET
//    @Path("/queries")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public CompletionStage<List<World>> queries(@QueryParam("queries") String queries) {
//        var worlds = new CompletableFuture[parseQueryCount(queries)];
//        var ret = new World[worlds.length];
//        Arrays.setAll(worlds, i -> {
//            return randomWorld().thenApply(w -> ret[i] = w);
//        });
//
//        return CompletableFuture.allOf(worlds).thenApply(v -> Arrays.asList(ret));
//    }
//
//    @GET
//    @Path("/updates")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public CompletionStage<List<World>> updates(@QueryParam("queries") String queries) {
//        var worlds = new CompletableFuture[parseQueryCount(queries)];
//        var ret = new World[worlds.length];
//        Arrays.setAll(worlds, i -> {
//            return randomWorld().thenApply(w -> {
//                w.setRandomNumber(randomWorldNumber());
//                ret[i] = w;
//                return w;
//            });
//        });
//
//        return CompletableFuture.allOf(worlds).thenCompose(v -> worldRepository.update(ret)).thenApply(v -> Arrays.asList(ret));
//    }

    private CompletionStage<World> randomWorld() {
        return worldRepository.find(randomWorldNumber());
    }

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
}
