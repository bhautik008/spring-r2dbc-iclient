package org.reactive.r2dbc.iclient.core;

import org.reactive.r2dbc.iclient.config.R2dbcConfiguration;
import org.reactive.r2dbc.iclient.exception.R2dbcConnectionException;
import org.springframework.r2dbc.core.DatabaseClient;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ValidationDepth;
import reactor.core.publisher.Mono;

/**
 * Builder class to create {@link R2dbcSqlSessionFactory} using
 * {@link ConnectionFactory}.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcSqlSessionFactoryBuilder {

	@SuppressWarnings("unchecked")
	public static R2dbcSqlSessionFactory build(ConnectionFactory connectionFactory) {

		// Validating Database connection. Application wasn't getting terminated without
		// block() call.
		Mono<Connection> conn = (Mono<Connection>) connectionFactory.create();
		conn.map(c -> c.validate(ValidationDepth.REMOTE))
				.onErrorMap(ex -> new R2dbcConnectionException("Issue connecting database.", ex)).block();

		DatabaseClient databaseClient = DatabaseClient.builder().connectionFactory(connectionFactory)
				.namedParameters(true).build();

		R2dbcConfiguration configuration = new R2dbcConfiguration(databaseClient);

		R2dbcSqlSessionFactory sessionFactory = new DefaultR2dbcSqlSessionFactory(connectionFactory, configuration);

		return sessionFactory;
	}

}
