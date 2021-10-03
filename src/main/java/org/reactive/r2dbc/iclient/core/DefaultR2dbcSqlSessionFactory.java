package org.reactive.r2dbc.iclient.core;

import java.io.IOException;

import org.reactive.r2dbc.iclient.config.R2dbcConfiguration;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.spi.ConnectionFactory;

/**
 * Default implementation of {@link R2dbcSqlSessionFactory}.
 * 
 * @author Bhautik Bhanani
 */
class DefaultR2dbcSqlSessionFactory implements R2dbcSqlSessionFactory {

	private final R2dbcConfiguration configuration;
	private final ConnectionFactory connectionFactory;
	private final R2dbcSqlSession r2dbcSqlSession;

	public DefaultR2dbcSqlSessionFactory(ConnectionFactory connectionFactory, R2dbcConfiguration configuration) {
		this.connectionFactory = connectionFactory;
		this.configuration = configuration;
		this.r2dbcSqlSession = new DefaultR2dbcSqlSession(configuration);
	}

	@Override
	public R2dbcSqlSession openSession() {
		return r2dbcSqlSession;
	}

	@Override
	public R2dbcConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	@Override
	public void close() throws IOException {
		if (this.connectionFactory instanceof ConnectionPool) {
			ConnectionPool connectionPool = ((ConnectionPool) this.connectionFactory);
			if (!connectionPool.isDisposed()) {
				connectionPool.dispose();
			}
		}
	}
}
