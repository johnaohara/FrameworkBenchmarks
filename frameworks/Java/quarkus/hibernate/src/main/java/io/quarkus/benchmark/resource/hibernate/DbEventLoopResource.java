package io.quarkus.benchmark.resource.hibernate;

import io.quarkus.benchmark.model.hibernate.World;
import io.quarkus.benchmark.repository.hibernate.WorldRepository;
import io.quarkus.benchmark.resource.BaseResource;
import io.quarkus.vertx.web.Route;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


@Singleton

public class DbEventLoopResource extends BaseResource {

    @Inject
    WorldRepository worldRepository;

    @Route(path = "/eventloop/db", methods = HttpMethod.GET)
    public void db(final RoutingContext ctx) {
        World world = randomWorldForRead();

        if (world==null)
            ctx.response()
                    .setStatusCode(404)
                    .end();
        else
            ctx.response()
                    .putHeader(HttpHeaders.SERVER, SERVER)
                    .putHeader(HttpHeaders.DATE, date)
                    .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                    .end(Json.encodeToBuffer(world));

    }


    private World randomWorldForRead() {
        return worldRepository.findSingleAndStateless(randomWorldNumber());
    }

    private Collection<World> randomWorldForRead(int count) {
        Set<Integer> ids = new HashSet<>(count);
        int counter = 0;
        while (counter < count) {
            counter += ids.add(Integer.valueOf(randomWorldNumber())) ? 1 : 0;
        }
        return worldRepository.findReadonly(ids);
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
