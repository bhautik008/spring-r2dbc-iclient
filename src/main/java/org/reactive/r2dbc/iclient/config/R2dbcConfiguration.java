package org.reactive.r2dbc.iclient.config;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.isTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.reactive.r2dbc.iclient.annotation.Result;
import org.reactive.r2dbc.iclient.annotation.Results;
import org.reactive.r2dbc.iclient.mapping.ResultMap;
import org.reactive.r2dbc.iclient.mapping.ResultMapping;
import org.reactive.r2dbc.iclient.proxy.R2dbcMapperProxyFactory;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.util.ClassUtils;

/**
 * Configuration class to hold mappers, result mapping and {@link DatabaseClient}
 * 
 * @author Bhautik Bhanani
 */
@SuppressWarnings("unchecked")
public class R2dbcConfiguration {

	private final DatabaseClient databaseClient;
	private Map<Class<?>, R2dbcMapperProxyFactory<?>> mappers = new HashMap<>();
	private Map<String, ResultMap<?>> resultMaps = new HashMap<>();

	public R2dbcConfiguration(DatabaseClient databaseClient) {
		super();
		this.databaseClient = databaseClient;
	}

	public DatabaseClient getDatabaseClient() {
		return databaseClient;
	}

	public <T> void addMapper(R2dbcMapperProxyFactory<T> mapper) {
		mappers.put(mapper.getMapperInterface(), mapper);
		parseMapper(mapper.getMapperInterface());
	}

	public <T> R2dbcMapperProxyFactory<T> getMapper(Class<T> type) {
		return (R2dbcMapperProxyFactory<T>) mappers.get(type);
	}

	public <T> ResultMap<T> getResultMap(String id) {
		return (ResultMap<T>) resultMaps.get(id);
	}

	private <T> void parseMapper(Class<T> type) {
		for (Method method : type.getMethods()) {
			if (method.isAnnotationPresent(Results.class)) {
				Results results = method.getAnnotation(Results.class);
				parseResultMap(results);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private void parseResultMap(Results results) {
		String id = results.id();
		Class<?> type = results.type();

		notNull(id, "Id is required for @Results");
		notNull(type, "Type is required for @Results with id: " + id);
		isTrue(ClassUtils.hasConstructor(type, new Class[0]),
				"Type requires non argument constructor for @Results with id: " + id);

		if (!resultMaps.containsKey(id)) {
			Result[] result = results.value();
			List<ResultMapping> resultMapping = new ArrayList<>();
			for (Result r : result) {
				resultMapping.add(ResultMapping.build(type, r.property(), r.column(), r.javaType(), r.typeConverter(),
						r.resultMap()));
			}
			ResultMap resultMap = new ResultMap.Builder(this, id, type, resultMapping).build();
			resultMaps.put(id, resultMap);
		}
	}
}
