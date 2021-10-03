package org.reactive.r2dbc.iclient.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.reactive.r2dbc.iclient.core.R2dbcSqlSession;

/**
 * Proxy factory to generate proxy of mapper interface using
 * {@link R2dbcSqlSession}. Register a mapper to {@link R2dbcConfiguration}.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcMapperProxyFactory<T> {

	private final Class<T> mapperInterface;
	private final Map<Method, R2dbcMapperProxy.MapperMethodInvoker> methodCache = new ConcurrentHashMap<>();

	public R2dbcMapperProxyFactory(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	public Class<T> getMapperInterface() {
		return mapperInterface;
	}

	public Map<Method, R2dbcMapperProxy.MapperMethodInvoker> getMethodCache() {
		return methodCache;
	}

	@SuppressWarnings("unchecked")
	protected T newInstance(R2dbcMapperProxy<T> mapperProxy) {
		return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface },
				mapperProxy);
	}

	public T newInstance(R2dbcSqlSession sqlSession) {
		final R2dbcMapperProxy<T> mapperProxy = new R2dbcMapperProxy<T>(sqlSession, mapperInterface, methodCache);
		return newInstance(mapperProxy);
	}

}
