package org.reactive.r2dbc.iclient.core;

import org.reactive.r2dbc.iclient.proxy.R2dbcMapperMethod;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The primary Java interface for working with R2dbc iClient. Through this
 * interface you can execute commands, get mappers and manage transactions.
 * 
 * @author Bhautik Bhanani
 */
public interface R2dbcSqlSession {

	<T> T getMapper(Class<T> clazz);

	/**
	 * Retrieve a single row mapped using the mapper method.
	 * 
	 * @param Mono<T> Mono of the returned object type
	 * @param mapper  method signature
	 * @return Mono of mapped object
	 */
	default <T> Mono<T> selectOne(R2dbcMapperMethod method) {
		return selectOne(method, null);
	}

	/**
	 * Retrieve a single row mapped using the mapper method and parameter.
	 * 
	 * @param Mono<T>   Mono of the returned object type
	 * @param mapper    method signature
	 * @param parameter A parameter object to pass to the statement.
	 * @return Mono of mapped object
	 */
	<T> Mono<T> selectOne(R2dbcMapperMethod method, Object params);

	/**
	 * Retrieve a list of mapped objects from the mapper method.
	 * 
	 * @param Flux<T> Flux of the returned object type
	 * @param mapper  method signature.
	 * @return Flux of mapped object
	 */
	default <T> Flux<T> select(R2dbcMapperMethod method) {
		return select(method, null);
	}

	/**
	 * Retrieve a list of mapped objects from the mapper method and parameter.
	 * 
	 * @param Flux<T>   Flux of the returned object type
	 * @param mapper    method signature.
	 * @param parameter A parameter object to pass to the statement.
	 * @return Flux of mapped object
	 */
	<T> Flux<T> select(R2dbcMapperMethod method, Object params);

	/**
	 * Execute an insert statement.
	 * 
	 * @param mapper method signature.
	 * @return Mono<T> The number of rows affected by the insert or resulted column.
	 */
	default <T> Mono<T> insert(R2dbcMapperMethod method) {
		return insert(method, null);
	}

	/**
	 * Execute an insert statement passing given parameter.
	 * 
	 * @param mapper    method signature.
	 * @param parameter A parameter object to pass to the statement.
	 * @return Mono<T> The number of rows affected by the insert or resulted column.
	 */
	<T> Mono<T> insert(R2dbcMapperMethod method, Object params);

	/**
	 * Execute an update statement. The number of rows affected will be returned.
	 * 
	 * @param mapper method signature.
	 * @return Mono<Integer> The number of rows affected by the update.
	 */
	default Mono<Integer> update(R2dbcMapperMethod method) {
		return update(method, null);
	}

	/**
	 * Execute an update statement passing given parameters. The number of rows
	 * affected will be returned.
	 * 
	 * @param mapper    method signature.
	 * @param parameter A parameter object to pass to the statement.
	 * @return Mono<Integer> The number of rows affected by the update.
	 */
	Mono<Integer> update(R2dbcMapperMethod method, Object params);

	/**
	 * Execute a delete statement. The number of rows affected will be returned.
	 * 
	 * @param mapper method signature.
	 * @return Mono<Integer> The number of rows affected by the delete.
	 */
	default Mono<Integer> delete(R2dbcMapperMethod method) {
		return delete(method, null);
	}

	/**
	 * Execute an delete statement passing given parameters. The number of rows
	 * affected will be returned.
	 * 
	 * @param mapper    method signature.
	 * @param parameter A parameter object to pass to the statement.
	 * @return Mono<Integer> The number of rows affected by the delete.
	 */
	Mono<Integer> delete(R2dbcMapperMethod method, Object params);
}
