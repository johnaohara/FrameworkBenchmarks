package io.quarkus.benchmark.resource.pgclient;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.benchmark.model.World;
import io.quarkus.benchmark.repository.pgclient.WorldRepository;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;


@ApplicationScoped
public class DbResource extends BaseResource {

    @Inject
    WorldRepository worldRepository;

    @Route(path = "db")
    public void db(RoutingContext rc) {
        randomWorld().thenAccept(world -> sendJson(rc, world)).exceptionally(t -> handleFail(rc, t));
    }

    @Route(path = "queries")
    public void queries(RoutingContext rc) {
        var queries = rc.request().getParam("queries");
        var worlds = new CompletableFuture[parseQueryCount(queries)];
        var ret = new World[worlds.length];
        Arrays.setAll(worlds, i -> {
            return randomWorld().thenApply(w -> ret[i] = w);
        });

        CompletableFuture.allOf(worlds).thenApply(v -> Arrays.asList(ret))
                .thenAccept(list -> sendJson(rc, list))
                .exceptionally(t -> handleFail(rc, t));
    }

    @Route(path = "updates")
    public void updates(RoutingContext rc) {
        var queries = rc.request().getParam("queries");
        var worlds = new CompletableFuture[parseQueryCount(queries)];
        var ret = new World[worlds.length];
        Arrays.setAll(worlds, i -> {
            return randomWorld().thenApply(w -> {
                w.setRandomNumber(randomWorldNumber());
                ret[i] = w;
                return w;
            });
        });

        CompletableFuture.allOf(worlds)
            .thenCompose(v -> worldRepository.update(ret)).thenApply(v -> Arrays.asList(ret))
            .thenAccept(list -> sendJson(rc, list))
            .exceptionally(t -> handleFail(rc, t));
    }

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