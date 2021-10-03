package org.reactive.r2dbc.iclient.reflection;

import java.util.List;
import java.util.Map;

import org.reactive.r2dbc.iclient.exception.R2dbcReflectionException;

/**
 * Base wrapper to retrieve a value from method arguments.
 * 
 * @author Bhautik Bhanani
 */
public abstract class BaseWrapper implements ObjectWrapper{

	private static final String EMPTY_STRING = "";
	protected final ParamObject paramObject;

	public BaseWrapper(ParamObject paramObject) {
		this.paramObject = paramObject;
	}

	protected Object resolveCollection(PropertyTokenizer property, Object object) {
		if (EMPTY_STRING.equals(property.getName())) {
			return object;
		} else {
			return paramObject.getValue(property.getName());
		}
	}

	@SuppressWarnings("rawtypes")
	protected Object getCollectionValue(PropertyTokenizer prop, Object collection) {
		if (collection instanceof Map) {
			return ((Map) collection).get(prop.getIndex());
		} else {
			int i = Integer.parseInt(prop.getIndex());
			if (collection instanceof List) {
				return ((List) collection).get(i);
			} else if (collection instanceof Object[]) {
				return ((Object[]) collection)[i];
			} else if (collection instanceof char[]) {
				return ((char[]) collection)[i];
			} else if (collection instanceof boolean[]) {
				return ((boolean[]) collection)[i];
			} else if (collection instanceof byte[]) {
				return ((byte[]) collection)[i];
			} else if (collection instanceof double[]) {
				return ((double[]) collection)[i];
			} else if (collection instanceof float[]) {
				return ((float[]) collection)[i];
			} else if (collection instanceof int[]) {
				return ((int[]) collection)[i];
			} else if (collection instanceof long[]) {
				return ((long[]) collection)[i];
			} else if (collection instanceof short[]) {
				return ((short[]) collection)[i];
			} else {
				throw new R2dbcReflectionException(
						"The '" + prop.getName() + "' property of " + collection + " is not a List or Array.");
			}
		}
	}

}
