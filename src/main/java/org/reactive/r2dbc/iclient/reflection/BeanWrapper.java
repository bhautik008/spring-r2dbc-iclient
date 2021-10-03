package org.reactive.r2dbc.iclient.reflection;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;
import org.reactive.r2dbc.iclient.exception.R2dbcReflectionException;

/**
 * Wrapper to retrieve a value from Bean.
 * 
 * @author Bhautik Bhanani
 */
public class BeanWrapper extends BaseWrapper {

	private final Object object;

	public BeanWrapper(ParamObject paramObject, Object object) {
		super(paramObject);
		this.object = object;
	}

	@Override
	public Object get(PropertyTokenizer property) {
		if (property.getIndex() != null) {
			Object collection = resolveCollection(property, object);
			return getCollectionValue(property, collection);
		} else {
			try {
				// return FieldUtils.readField(object, property.getName(), Boolean.TRUE);
				return PropertyUtils.getProperty(object, property.getName());
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new R2dbcReflectionException("Could not get property '" + property.getName() + "' from "
						+ (object != null ? object.getClass() : "null object") + ". Cause: " + e.toString(), e);
			}
		}
	}

}
