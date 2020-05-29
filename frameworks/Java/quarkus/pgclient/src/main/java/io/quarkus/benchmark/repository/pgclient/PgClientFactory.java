package io.quarkus.benchmark.repository.pgclient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.PoolOptions;

@ApplicationScoped
public class PgClientFactory {

	@ConfigProperty(name = "quarkus.datasource.url")
	String url;

	@ConfigProperty(name = "quarkus.datasource.username")
	String user;

	@ConfigProperty(name = "quarkus.datasource.password")
	String pass;

	@Inject
	Vertx vertx;

	@Produces
	@ApplicationScoped
	public PgClients pgClients() {
	    return new PgClients(this);
	}


	PgPool sqlClient(int size) {
		PoolOptions options = new PoolOptions();
		PgConnectOptions connectOptions = new PgConnectOptions();
		// vertx-reactive:postgresql://tfb-database:5432/hello_world
		Matcher matcher = Pattern.compile("vertx-reactive:postgresql://([-a-zA-Z]+):([0-9]+)/(.*)").matcher(url);
		matcher.matches();
		connectOptions.setDatabase(matcher.group(3));
		connectOptions.setHost(matcher.group(1));
		connectOptions.setPort(Integer.parseInt(matcher.group(2)));
		connectOptions.setUser(user);
		connectOptions.setPassword(pass);
		connectOptions.setCachePreparedStatements(true);
		options.setMaxSize(size);
		return PgPool.pool(vertx, connectOptions, options);
	}
}