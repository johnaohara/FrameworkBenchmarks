package io.quarkus.benchmark.filter;

import io.quarkus.rest.ContainerResponseFilter;
import io.quarkus.rest.server.runtime.spi.SimplifiedResourceInfo;
import io.quarkus.scheduler.Scheduled;

import javax.inject.Singleton;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Singleton
public class ServerHeaderFilter {

    private String date;

    @Scheduled(every="1s")
    void increment() {
        date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
    }

//    @ContainerResponseFilter
    public void filter(SimplifiedResourceInfo simplifiedResourceInfo, ContainerResponseContext responseContext,
                       ContainerRequestContext requestContext, Throwable t) {
        final MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        headers.add( "Server", "Quarkus");
        headers.add( "Date", date);
    }
}