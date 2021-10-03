package org.reactive.r2dbc.iclient.config;

import static org.springframework.util.Assert.hasText;

import org.reactive.r2dbc.iclient.core.R2dbcSqlSession;
import org.reactive.r2dbc.iclient.core.R2dbcSqlSessionFactory;
import org.reactive.r2dbc.iclient.core.R2dbcSqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.miku.r2dbc.mysql.MySqlConnectionConfiguration;
import dev.miku.r2dbc.mysql.MySqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;

/**
 * Default configuration class to initialize {@link ConnectionFactory},
 * {@link R2dbcSqlSessionFactory} if no bean provided.
 * 
 * Require properties, if no {@link ConnectionFactory} bean initialized:
 * "spring.r2dbc.host", "spring.r2dbc.port", "spring.r2dbc.username",
 * "spring.r2dbc.password", "spring.r2dbc.database"
 * 
 * @author Bhautik Bhanani
 */
@Configuration
@ConditionalOnClass({ R2dbcSqlSessionFactory.class })
public class R2dbcDefaultDBConfig {

	private Logger log = LoggerFactory.getLogger(R2dbcDefaultDBConfig.class);

	@Value("${spring.r2dbc.host:}")
	private String dbHost;
	@Value("${spring.r2dbc.port:}")
	private String dbPort;
	@Value("${spring.r2dbc.username:}")
	private String dbUsername;
	@Value("${spring.r2dbc.password:}")
	private String dbPassword;
	@Value("${spring.r2dbc.database:}")
	private String dbDatabase;

	/**
	 * Initialize default {@link ConnectionFactory} based on provided properties.
	 * 
	 * @return ConnectionFactory
	 */
	@Bean("connectionFactory")
	@ConditionalOnMissingBean
	public ConnectionFactory connectionFactory() {
		log.debug("Initializing default MySQL ConnectionFactory.");
		
		hasText(dbHost, "Host is require for database connection. Please define 'spring.r2dbc.host' property.");
		hasText(dbPort, "Port is require for database connection. Please define 'spring.r2dbc.port' property.");
		hasText(dbUsername,
				"Username is require for database connection. Please define 'spring.r2dbc.username' property.");
		hasText(dbPassword,
				"Password is require for database connection. Please define 'spring.r2dbc.password' property.");
		hasText(dbDatabase,
				"Database name is require for database connection. Please define 'spring.r2dbc.database' property.");

		MySqlConnectionConfiguration configuration = MySqlConnectionConfiguration.builder().host(dbHost)
				.port(Integer.parseInt(dbPort)).user(dbUsername).password(dbPassword).database(dbDatabase).build();
		
		return MySqlConnectionFactory.from(configuration);
	}

	/**
	 * Initialize default {@link R2dbcSqlSessionFactory} bean.
	 * 
	 * @param {@link ConnectionFactory} bean
	 * @return R2dbcSqlSessionFactory
	 */
	@Bean("r2dbcSqlSessionFactory")
	@ConditionalOnMissingBean
	public R2dbcSqlSessionFactory r2dbcSqlSessionFactory(ConnectionFactory connectionFactory) {
		log.debug("Initializing default R2dbcSqlSessionFactory.");
		return R2dbcSqlSessionFactoryBuilder.build(connectionFactory);
	}

	/**
	 * Initialize default {@link R2dbcSqlSession} bean.
	 * 
	 * @param {@link R2dbcSqlSessionFactory} bean
	 * @return R2dbcSqlSession
	 */
	@Bean("r2dbcSqlSession")
	@ConditionalOnMissingBean
	public R2dbcSqlSession r2dbcSqlSession(R2dbcSqlSessionFactory r2dbcSqlSessionFactory) {
		log.debug("Initializing default R2dbcSqlSession.");
		return r2dbcSqlSessionFactory.openSession();
	}

}
