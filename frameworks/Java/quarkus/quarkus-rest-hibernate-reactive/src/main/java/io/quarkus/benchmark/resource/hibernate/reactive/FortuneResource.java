package io.quarkus.benchmark.resource.hibernate.reactive;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import io.quarkus.benchmark.repository.hibernate.reactive.FortuneRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class FortuneResource extends BaseResource {

    @Inject
    FortuneRepository repository;

    private final Mustache template;

    public FortuneResource() {
        MustacheFactory mf = new DefaultMustacheFactory();
        template = mf.compile("fortunes.mustache");
    }

//    @GET
//    @Path("fortunes")
//    public Uni<List<Fortune>> fortunes(RoutingContext rc) {
//        repository.findAll()
//        .subscribe().with( fortunes -> {
//            fortunes.add(new Fortune(0, "Additional fortune added at request time."));
//            fortunes.sort(Comparator.comparing(fortune -> fortune.getMessage()));
//            StringWriter writer = new StringWriter();
//            template.execute(writer, Collections.singletonMap("fortunes", fortunes));
//            rc.response().putHeader("Content-Type", "text/html;charset=UTF-8");
//            rc.response().end(writer.toString());
//        },
//                           t -> handleFail(rc, t));
//    }
}
