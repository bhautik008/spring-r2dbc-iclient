package org.reactive.r2dbc.iclient.reflection;

/**
 * Object wrapper to retrieve value from method arguments.
 * 
 * @author Bhautik Bhanani
 */
public interface ObjectWrapper {

	public Object get(PropertyTokenizer property);
}
