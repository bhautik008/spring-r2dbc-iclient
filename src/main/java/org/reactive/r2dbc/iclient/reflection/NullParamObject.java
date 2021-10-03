package org.reactive.r2dbc.iclient.reflection;

/**
 * Default implementation of NULL for ParamObject.
 * <p>
 * Note: Internal use only.
 * 
 * @author Bhautik Bhanani
 */
public class NullParamObject {
	public static final ParamObject NULL_PARAM_OBJECT = ParamObject.forObject(NullObject.class);

	class NullObject {
	}
}