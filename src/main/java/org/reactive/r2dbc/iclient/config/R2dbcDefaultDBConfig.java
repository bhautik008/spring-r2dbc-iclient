package org.reactive.r2dbc.iclient.config;

import static org.springframework.util.Assert.hasText;
import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

import org.apache.commons.lang3.StringUtils;
import org.reactive.r2dbc.iclient.core.R2dbcSqlSession;
import org.reactive.r2dbc.iclient.core.R2dbcSqlSessionFactory;
import org.reactive.r2dbc.iclient.core.R2dbcSqlSessionFactoryBuilder;
import org.reactive.r2dbc.iclient.type.DatabaseTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;

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
	@Value("${spring.r2dbc.dbtype:}")
	private String dbType;
	@Value("${spring.r2dbc.options:}")
	private String dbOptions;

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
		hasText(dbType,
				"Database Type is require for database connection. Please define 'spring.r2dbc.database' property with either of these values [mysql, postgresql, mariadb].");

		ConnectionFactoryOptions.Builder builder = ConnectionFactoryOptions.builder().option(HOST, dbHost)
				.option(PORT, Integer.valueOf(dbPort))
				.option(DRIVER, DatabaseTypes.getDatabaseType(dbType).getDatabaseType())
				.option(DATABASE, dbDatabase)
				.option(USER, dbUsername)
				.option(PASSWORD, dbPassword);
		
		if (StringUtils.isNotBlank(dbOptions)) {
			String[] options = StringUtils.split(dbOptions, ",");
			if (options != null && options.length > 0) {
				for (String option : options) {
					String[] op = StringUtils.split(option, ":");
					if (op != null && op.length == 2) {
						builder = builder.option(Option.valueOf(op[0]), op[1]);
					}
				}
			}
		}
		
		return ConnectionFactories.get(builder.build());
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
