package io.quarkus.benchmark.resource.pgclient;

import io.reactiverse.pgclient.PgClient;
import io.reactiverse.pgclient.PgPool;
import io.reactiverse.pgclient.PgPoolOptions;
import io.vertx.core.Vertx;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Singleton
class PgClients {
    private final Iterator<PgClient> iterator;


    @Inject
    public PgClients(Vertx vertx) {
        List<PgClient> clients = new ArrayList<>();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            clients.add(pgClient(vertx));
        }
        this.iterator = Stream.generate(() -> clients).flatMap(Collection::stream).iterator();
    }

    synchronized PgClient getOne() {
        return iterator.next();
    }

    private PgPool pgClient(Vertx vertx) {
        PgPoolOptions options = new PgPoolOptions();
        options.setDatabase("hello_world");
        options.setHost("localhost");
        options.setPort(5432);
        options.setUser("benchmarkdbuser");
        options.setPassword("benchmarkdbpass");
        options.setCachePreparedStatements(true);
        options.setMaxSize(1);
        return PgClient.pool(vertx, options);
    }
}
