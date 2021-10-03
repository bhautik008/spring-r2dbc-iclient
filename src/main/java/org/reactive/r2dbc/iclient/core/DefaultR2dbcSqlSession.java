package org.reactive.r2dbc.iclient.core;

import org.apache.commons.lang3.StringUtils;
import org.reactive.r2dbc.iclient.config.R2dbcConfiguration;
import org.reactive.r2dbc.iclient.mapping.R2dbcPreparedStatement;
import org.reactive.r2dbc.iclient.proxy.R2dbcMapperMethod;
import org.reactive.r2dbc.iclient.proxy.R2dbcMapperProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Default implementation of {@link R2dbcSqlSession}.
 * 
 * @author Bhautik Bhanani
 */
@SuppressWarnings("unchecked")
class DefaultR2dbcSqlSession implements R2dbcSqlSession {

	private static Logger log = LoggerFactory.getLogger(R2dbcSqlSession.class);
	private final R2dbcConfiguration configuration;
	private final DatabaseClient databaseClient;

	public DefaultR2dbcSqlSession(R2dbcConfiguration configuration) {
		this.configuration = configuration;
		this.databaseClient = configuration.getDatabaseClient();
	}

	@Override
	public <T> T getMapper(Class<T> clazz) {
		R2dbcMapperProxyFactory<T> mapperFactory = new R2dbcMapperProxyFactory<>(clazz);
		configuration.addMapper(mapperFactory);
		return mapperFactory.newInstance(this);
	}

	@Override
	public <T> Mono<T> selectOne(R2dbcMapperMethod method, Object params) {
		log.info("Executing select statment for: {}", method.getCommand().getStatement());
		Mono<T> result = null;
		GenericExecuteSpec sql = new R2dbcPreparedStatement(databaseClient, method).parameterize(params);
		if (StringUtils.isNotBlank(method.getMethod().getResultMap())
				&& configuration.getResultMap(method.getMethod().getResultMap()) != null) {
			result = (Mono<T>) sql.map(configuration.getResultMap(method.getMethod().getResultMap()).getMapper()).one();
		} else {
			result = (Mono<T>) sql.fetch().first();
		}
		return result;
	}

	@Override
	public <T> Flux<T> select(R2dbcMapperMethod method, Object params) {
		log.info("Executing select statment for: {}", method.getCommand().getStatement());
		Flux<T> result = null;
		GenericExecuteSpec sql = new R2dbcPreparedStatement(databaseClient, method).parameterize(params);
		if (StringUtils.isNotBlank(method.getMethod().getResultMap())
				&& configuration.getResultMap(method.getMethod().getResultMap()) != null) {
			result = (Flux<T>) sql.map(configuration.getResultMap(method.getMethod().getResultMap()).getMapper()).all();
		} else {
			result = (Flux<T>) sql.fetch().all();
		}
		return result;
	}

	@Override
	public <T> Mono<T> insert(R2dbcMapperMethod method, Object params) {
		log.info("Executing insert statment for: {}", method.getCommand().getStatement());
		GenericExecuteSpec sql = new R2dbcPreparedStatement(databaseClient, method).parameterize(params);
		if (method.getCommand().isRetrieveId()) {
			sql = sql.filter((statement, executeFunction) -> statement
					.returnGeneratedValues(method.getCommand().getIdColumn()).execute());
		}

		Mono<T> result = null;
		if (method.getCommand().isRetrieveId()) {
			result = (Mono<T>) sql.fetch().first().map(row -> row.get(method.getCommand().getIdColumn()));
		} else {
			result = (Mono<T>) sql.fetch().rowsUpdated();
		}
		return result;
	}

	@Override
	public Mono<Integer> update(R2dbcMapperMethod method, Object params) {
		log.info("Executing update statment for: {}", method.getCommand().getStatement());
		GenericExecuteSpec sql = new R2dbcPreparedStatement(databaseClient, method).parameterize(params);
		return sql.fetch().rowsUpdated();
	}

	@Override
	public Mono<Integer> delete(R2dbcMapperMethod method, Object params) {
		log.info("Executing delete statment for: {}", method.getCommand().getStatement());
		GenericExecuteSpec sql = new R2dbcPreparedStatement(databaseClient, method).parameterize(params);
		return sql.fetch().rowsUpdated();
	}

//	private GenericExecuteSpec getExecuteSpec(R2dbcMapperMethod method, Object[] params) {
//		GenericExecuteSpec sql = databaseClient.sql(method.getCommand().getStatement());
//		if (params != null && params.length > 0) {
//			log.info("Passing parameters: {}", Arrays.toString(params));
//			String[] paramNames = method.getMethod().getParamNameResolver().getNames();
//			if (paramNames.length > 0 && paramNames.length == params.length) {
//				for (int i = 0; i < params.length; i++) {
//					PropertyTokenizer tokenizer = method.getCommand().getPropertyMapper().get(paramNames[i]);
//					if (tokenizer != null) {
//						Object paramValue = ParamObject.forObject(params[i]).getValue(tokenizer);
//						if (paramValue != null) {
//							sql = sql.bind(tokenizer.getOriginalProperty(), paramValue);
//						} else {
//							sql = sql.bindNull(tokenizer.getOriginalProperty(), params[i].getClass());
//						}
//					} else {
//						sql = sql.bind(paramNames[i], params[i]);
//					}
//				}
//			} else {
//				for (int i = 0; i < params.length; i++) {
//					sql = sql.bind(i, params[i]);
//				}
//			}
//		}
//		return sql;
//	}
}
