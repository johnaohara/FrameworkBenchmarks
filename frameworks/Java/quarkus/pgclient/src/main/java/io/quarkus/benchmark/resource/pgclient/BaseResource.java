package io.quarkus.benchmark.resource.pgclient;

import javax.inject.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.ext.web.RoutingContext;

public abstract class BaseResource {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";

    @Inject
    ObjectMapper mapper;

    void sendJson(RoutingContext rc, Object value) {
        try {
            rc.response().putHeader(CONTENT_TYPE, APPLICATION_JSON);
            rc.response().end(mapper.writeValueAsString(value));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }   
    }

    Void handleFail(RoutingContext rc, Throwable t) {
        rc.response().setStatusCode(500).end(t.toString());
        return null;
    }

}
