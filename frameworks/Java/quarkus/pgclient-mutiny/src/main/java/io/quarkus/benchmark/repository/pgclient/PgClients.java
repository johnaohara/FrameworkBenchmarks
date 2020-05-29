package io.quarkus.benchmark.repository.pgclient;

import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.SqlClient;

import javax.enterprise.context.ApplicationScoped;

public class PgClients {
    private ThreadLocal<SqlClient> sqlClient = new ThreadLocal<>();
    private ThreadLocal<PgPool> pool = new ThreadLocal<>();
    private PgClientFactory pgClientFactory;


    // for ArC
    public PgClients() {
    }

    public PgClients(PgClientFactory pgClientFactory) {
        this.pgClientFactory = pgClientFactory;
    }

    SqlClient getClient() {
        SqlClient ret = sqlClient.get();
        if (ret == null) {
            ret = pgClientFactory.sqlClient(1);
            sqlClient.set(ret);
        }
        return ret;
    }

    synchronized PgPool getPool() {
        PgPool ret = pool.get();
        if (ret == null) {
            ret = pgClientFactory.sqlClient(4);
            pool.set(ret);
        }
        return ret;
    }
}
