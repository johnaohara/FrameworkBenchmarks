package io.quarkus.benchmark.model;

import io.vertx.mutiny.sqlclient.Row;

import java.util.Objects;

public class World {

    private int id;
    private int randomNumber;

    public World() {}

    public World(int id, int randomNumber) {
        this.id = id;
        this.randomNumber = randomNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public static World from(Row row) {
        return new World(row.getInteger(0), row.getInteger(1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        World world = (World) o;
        return id == world.id &&
                randomNumber == world.randomNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, randomNumber);
    }
}
