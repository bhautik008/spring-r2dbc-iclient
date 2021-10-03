package org.reactive.r2dbc.iclient.reflection;

import java.util.Map;

/**
 * Wrapper to retrieve value from Map, collection or array.
 * 
 * @author Bhautik Bhanani
 */
public class MapWrapper extends BaseWrapper {

	private final Map<String, Object> map;

	public MapWrapper(ParamObject paramObject, Map<String, Object> map) {
		super(paramObject);
		this.map = map;
	}

	@Override
	public Object get(PropertyTokenizer property) {
		if (property.getIndex() != null) {
			Object collection = resolveCollection(property, map);
			return getCollectionValue(property, collection);
		}
		return map.get(property.getName());
	}

}
