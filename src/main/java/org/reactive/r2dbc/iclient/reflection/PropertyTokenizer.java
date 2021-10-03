package org.reactive.r2dbc.iclient.reflection;

import java.util.Iterator;

/**
 * Parse property in a query.
 * 
 * @author Bhautik Bhanani
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {

	private String originalProperty;
	private String name;
	private final String indexedName;
	private String index;
	private final String children;

	public PropertyTokenizer(String property) {
		originalProperty = property;
		int delim = property.indexOf('.');
		if (delim > -1) {
			name = property.substring(0, delim);
			children = property.substring(delim + 1);
		} else {
			name = property;
			children = null;
		}
		indexedName = name;
		delim = name.indexOf('[');
		if (delim > -1) {
			index = name.substring(delim + 1, name.length() - 1);
			name = name.substring(0, delim);
		}
	}

	public String getOriginalProperty() {
		return originalProperty;
	}

	public String getName() {
		return name;
	}

	public String getIndexedName() {
		return indexedName;
	}

	public String getIndex() {
		return index;
	}

	public String getChildren() {
		return children;
	}

	@Override
	public boolean hasNext() {
		return children != null;
	}

	@Override
	public PropertyTokenizer next() {
		return new PropertyTokenizer(children);
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException(
				"Remove is not supported, as it has no meaning in the context of properties.");
	}

}
