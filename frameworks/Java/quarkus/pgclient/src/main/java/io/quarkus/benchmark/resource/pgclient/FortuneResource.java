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

    @Inject
    FortuneRepository repository;

    @ResourcePath("fortunes")
    Template template;

    @Route(path = "fortunes")
    public void fortunes(RoutingContext rc) {
        repository.findAll()
        .subscribe().with( fortunes -> {
            fortunes.add(new Fortune(0, "Additional fortune added at request time."));
            fortunes.sort(Comparator.comparing(fortune -> fortune.getMessage()));
            rc.response().putHeader("Content-Type", "text/html;charset=UTF-8");
            rc.response().end(template.data("fortunes.html", fortunes).render());
        },
                           t -> handleFail(rc, t));
    }
}
