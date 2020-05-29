package io.quarkus.benchmark.filter;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import org.jboss.resteasy.util.HttpHeaderNames;

import javax.enterprise.event.Observes;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Provider
public class ServerHeaderFilter implements ContainerResponseFilter {

    protected static String date;

    private static final String SERVER = "Server";
    private static final String DATE = "Date";
    private static final String QUARKUS = "Quarkus";


    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        responseContext.getHeaders().add(SERVER, QUARKUS);
        responseContext.getHeaders().add(DATE, date);
    }


    void onStart(@Observes StartupEvent ev) {
        date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
    }

    @Scheduled(every="1s")
    public void increment() {
        date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now());
    }
}
