package org.reactive.r2dbc.iclient.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.reactive.r2dbc.iclient.annotation.Param;
import org.reactive.r2dbc.iclient.exception.R2dbcBindingException;
import org.reactive.r2dbc.iclient.exception.R2dbcReflectionException;

/**
 * Reads method arguments annotated with @Param annotation.
 * 
 * @author Bhautik Bhanani
 */
public class ParameterResolver {

	private final SortedMap<Integer, String> names;

	public ParameterResolver(Method method) {
		final Annotation[][] paramAnnotations = method.getParameterAnnotations();
		final SortedMap<Integer, String> map = new TreeMap<>();
		int paramCount = paramAnnotations.length;
		for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
			String name = null;
			for (Annotation annotation : paramAnnotations[paramIndex]) {
				if (annotation instanceof Param) {
					name = ((Param) annotation).value();
					break;
				}
			}
			if (name == null) {
				throw new R2dbcReflectionException(
						"Parameter name is required at index " + paramIndex + " for method " + method.getName());
			}
			map.put(paramIndex, name);
		}
		names = Collections.unmodifiableSortedMap(map);
	}

	public String[] getNames() {
		return names.values().toArray(new String[0]);
	}

	public Object getNamedParameter(Object[] args) {
		final int paramCount = names.size();
		if (args == null || paramCount == 0) {
			return null;
		} else {
			final Map<String, Object> param = new ParamMap<>();
			names.forEach((index, value) -> param.put(value, args[index]));
			return param;
		}
	}

	public static class ParamMap<V> extends HashMap<String, V> {

		private static final long serialVersionUID = -2212268410512043556L;

		@Override
		public V get(Object key) {
			if (!super.containsKey(key)) {
				throw new R2dbcBindingException(
						"Parameter '" + key + "' not found. Available parameters are " + keySet());
			}
			return super.get(key);
		}

	}
}
