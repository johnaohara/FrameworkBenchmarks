package io.quarkus.benchmark.repository.hibernate.reactive;

import java.util.function.Function;

import javax.inject.Inject;

import org.hibernate.FlushMode;
import org.hibernate.reactive.mutiny.Mutiny;

import io.smallrye.mutiny.Uni;

public class MutinyBaseRepository {
    @Inject
    protected Mutiny.SessionFactory sf;

    public <T> Uni<T> inSession(Function<Mutiny.Session, Uni<T>> work) {
        return sf.withSession(session -> work.apply(session));
    }

}
