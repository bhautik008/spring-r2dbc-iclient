package org.reactive.r2dbc.iclient.reflection;

import static org.reactive.r2dbc.iclient.reflection.NullParamObject.NULL_PARAM_OBJECT;

import java.util.Map;

/**
 * @author Bhautik Bhanani
 */
public class ParamObject {

	private ObjectWrapper objectWrapper;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ParamObject(Object object) {
		if (object instanceof Map) {
			objectWrapper = new MapWrapper(this, (Map) object);
		} else {
			objectWrapper = new BeanWrapper(this, object);
		}
	}

	public static ParamObject forObject(Object object) {
		if (object == null) {
			return NULL_PARAM_OBJECT;
		}
		return new ParamObject(object);
	}

	public Object getValue(PropertyTokenizer prop) {
		if (prop.hasNext()) {
			ParamObject paramObject = paramObjectForProperty(prop.getIndexedName());
			if (paramObject == NULL_PARAM_OBJECT) {
				return null;
			}
			return paramObject.getValue(prop.getChildren());
		}
		return objectWrapper.get(prop);
	}

	public Object getValue(String property) {
		PropertyTokenizer prop = new PropertyTokenizer(property);
		return getValue(prop);
	}

	private ParamObject paramObjectForProperty(String property) {
		Object value = getValue(property);
		return ParamObject.forObject(value);
	}

}
