package io.quarkus.benchmark.resource.pgclient;

import java.util.Comparator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.quarkus.benchmark.model.Fortune;
import io.quarkus.benchmark.repository.pgclient.FortuneRepository;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;
import io.quarkus.vertx.web.Route;
import io.vertx.ext.web.RoutingContext;

@ApplicationScoped
public class FortuneResource extends BaseResource {

    private static final String CONTENT_TYPE = "Content-type";
    private static final String TEXT_HTML = "text/html;charset=UTF-8";
    private static final String FORTUNES_KEY = "fortunes";

    @Inject
    FortuneRepository repository;

    @ResourcePath("fortunes")
    Template template;

    @Route(path = "fortunes")
    public void fortunes(RoutingContext rc) {
        repository.findAll()
                .subscribe().with(fortunes -> {
                    fortunes.add(new Fortune(0, "Additional fortune added at request time."));
                    fortunes.sort(Comparator.comparing(fortune -> fortune.getMessage()));
                    rc.response().putHeader(CONTENT_TYPE, TEXT_HTML);
                    template.data(FORTUNES_KEY, fortunes).renderAsync().exceptionally(err -> {
                        handleFail(rc, err);
                        return null;
                    }).thenAccept(result -> {
                        rc.response().end(result);
                    });
                },
                t -> handleFail(rc, t));
    }
}
