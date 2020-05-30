package io.quarkus.benchmark.resource.pgclient;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Singleton;

import io.quarkus.scheduler.Scheduled;
import io.quarkus.vertx.web.RouteFilter;
import io.vertx.ext.web.RoutingContext;

@Singleton
public class ServerHeaderFilter {

    private String date;

    private static final String SERVER = "Server";
    private static final String QUARKUS = "Quarkus";
    private static final String DATE = "Date";
    private static final String CONNECTION = "Connection";
    private static final String KEEP_ALIVE = "keep-alive";

    @Scheduled(every = "1s")
    void increment() {
        date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
    }

    @RouteFilter(100)
    void myFilter(RoutingContext rc) {
        rc.response().putHeader(SERVER, QUARKUS);
        rc.response().putHeader(DATE, date);
        rc.response().putHeader(CONNECTION, KEEP_ALIVE);
        rc.next();
    }
}
