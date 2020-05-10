package io.quarkus.benchmark.resource;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.event.Observes;
import javax.ws.rs.core.MediaType;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class BaseResource {
    protected static final String APPLICATION_JSON = MediaType.APPLICATION_JSON;

    protected static final String SERVER = "Quarkus";

    protected static String date;

    void onStart(@Observes StartupEvent ev) {
        date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
    }

    @Scheduled(every="1s")
    void increment() {
        date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
    }

}
