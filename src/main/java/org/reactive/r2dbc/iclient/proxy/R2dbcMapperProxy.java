package org.reactive.r2dbc.iclient.proxy;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.reactive.r2dbc.iclient.core.R2dbcSqlSession;

/**
 * {@link InvocationHandler} for mapper method. Generates
 * {@link R2dbcMapperMethod} and cache for future calls.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcMapperProxy<T> implements InvocationHandler, Serializable {

	private static final long serialVersionUID = -697528651196046523L;

	private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
			| MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
	private static final Constructor<MethodHandles.Lookup> lookupConstructor;
	private static final Method privateLookupInMethod;
	private final R2dbcSqlSession sqlSession;
	private final Class<T> mapperInterface;
	private final Map<Method, MapperMethodInvoker> methodCache;

	static {
		Method privateLookupIn;
		try {
			privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
		} catch (NoSuchMethodException e) {
			privateLookupIn = null;
		}
		privateLookupInMethod = privateLookupIn;

		Constructor<MethodHandles.Lookup> lookup = null;
		if (privateLookupInMethod == null) {
			// JDK 1.8
			try {
				lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
				lookup.setAccessible(true);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException(
						"There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' method in java.lang.invoke.MethodHandles.",
						e);
			} catch (Exception e) {
				lookup = null;
			}
		}
		lookupConstructor = lookup;
	}

	public R2dbcMapperProxy(R2dbcSqlSession sqlSession, Class<T> mapperInterface,
			Map<Method, MapperMethodInvoker> methodCache) {
		this.sqlSession = sqlSession;
		this.mapperInterface = mapperInterface;
		this.methodCache = methodCache;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			if (Object.class.equals(method.getDeclaringClass())) {
				return method.invoke(this, args);
			} else {
				return cachedInvoker(method).invoke(proxy, method, args, sqlSession);
			}
		} catch (Throwable t) {
			throw t;
		}
	}

	private MapperMethodInvoker cachedInvoker(Method method) throws Throwable {
		try {
			return methodCache.computeIfAbsent(method, m -> {
				if (m.isDefault()) {
					try {
						if (privateLookupInMethod == null) {
							return new DefaultMethodInvoker(getMethodHandleJava8(method));
						} else {
							return new DefaultMethodInvoker(getMethodHandleJava9(method));
						}
					} catch (IllegalAccessException | InstantiationException | InvocationTargetException
							| NoSuchMethodException e) {
						throw new RuntimeException(e);
					}
				} else {
					return new PlainMethodInvoker(new R2dbcMapperMethod(mapperInterface, method));
				}
			});
		} catch (RuntimeException re) {
			Throwable cause = re.getCause();
			throw cause == null ? re : cause;
		}
	}

	private MethodHandle getMethodHandleJava9(Method method)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		final Class<?> declaringClass = method.getDeclaringClass();
		return ((MethodHandles.Lookup) privateLookupInMethod.invoke(null, declaringClass, MethodHandles.lookup()))
				.findSpecial(declaringClass, method.getName(),
						MethodType.methodType(method.getReturnType(), method.getParameterTypes()), declaringClass);
	}

	private MethodHandle getMethodHandleJava8(Method method)
			throws IllegalAccessException, InstantiationException, InvocationTargetException {
		final Class<?> declaringClass = method.getDeclaringClass();
		return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES).unreflectSpecial(method, declaringClass);
	}

	interface MapperMethodInvoker {
		Object invoke(Object proxy, Method method, Object[] args, R2dbcSqlSession sqlSession) throws Throwable;
	}

	private static class PlainMethodInvoker implements MapperMethodInvoker {
		private final R2dbcMapperMethod mapperMethod;

		public PlainMethodInvoker(R2dbcMapperMethod mapperMethod) {
			super();
			this.mapperMethod = mapperMethod;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args, R2dbcSqlSession sqlSession) throws Throwable {
			return mapperMethod.execute(sqlSession, args);
		}
	}

	private static class DefaultMethodInvoker implements MapperMethodInvoker {
		private final MethodHandle methodHandle;

		public DefaultMethodInvoker(MethodHandle methodHandle) {
			super();
			this.methodHandle = methodHandle;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args, R2dbcSqlSession sqlSession) throws Throwable {
			return methodHandle.bindTo(proxy).invokeWithArguments(args);
		}
	}
}
