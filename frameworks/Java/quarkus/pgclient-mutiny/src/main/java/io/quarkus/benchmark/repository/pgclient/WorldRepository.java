package io.quarkus.benchmark.repository.pgclient;

import io.quarkus.benchmark.model.World;
import io.reactivex.Single;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class WorldRepository {

    private static final String SELECT_WORLD = "SELECT id, randomnumber from WORLD where id=$1";

    @Inject
    PgClients pgClients;

    public Uni<World> find(int id) {

        return pgClients.getClient().preparedQuery(SELECT_WORLD, Tuple.of(id))
                .map(rows -> rows.iterator().next())
                .onItem().produceUni(row -> Uni.createFrom().item(World.from(row)));
    }


    public Uni<World> findRandom() {

        return find(randomWorldNumber());

    }

    public Single<World> update(World world) {


//        return client.rxPreparedQuery("UPDATE world SET randomnumber = $1 WHERE id = $2",
//                Tuple.of(world.getRandomNumber(), world.getId()))
//                .map(rows -> world)
//                .subscribeOn(scheduler);
        return null;
    }


    private int randomWorldNumber() {
        return 1 + ThreadLocalRandom.current().nextInt(10000);
    }
}
