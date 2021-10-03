package org.reactive.r2dbc.iclient.core;

import java.io.Closeable;

import org.reactive.r2dbc.iclient.config.R2dbcConfiguration;

import io.r2dbc.spi.ConnectionFactory;

/**
 * Creates an {@link R2dbcSqlSession} out of a {@link ConnectionFactory}.
 * 
 * @author Bhautik Bhanani
 */
public interface R2dbcSqlSessionFactory extends Closeable{

	R2dbcSqlSession openSession();
	
	R2dbcConfiguration getConfiguration();
	
	ConnectionFactory getConnectionFactory();
}
