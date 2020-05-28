package io.quarkus.benchmark.repository.pgclient;

import io.quarkus.benchmark.model.World;
import io.reactivex.Single;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class WorldRepository {



    @Inject
    PgPool client;

    public Uni<World> find(int id) {

        return client.preparedQuery("SELECT id, first_name, last_name  FROM person WHERE id = $1", Tuple.of(id))
                .map(rows -> rows.iterator().next())
                .onItem().produceUni( row -> Uni.createFrom().item(World.from(row)));
    }

    public Uni<Void> update(World[] world) {


//        return client.rxPreparedQuery("UPDATE world SET randomnumber = $1 WHERE id = $2",
//                Tuple.of(world.getRandomNumber(), world.getId()))
//                .map(rows -> world)
//                .subscribeOn(scheduler);
        return null;
    }
}
