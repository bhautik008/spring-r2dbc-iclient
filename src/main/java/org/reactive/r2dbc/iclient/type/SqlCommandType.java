package org.reactive.r2dbc.iclient.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.reactive.r2dbc.iclient.annotation.Delete;
import org.reactive.r2dbc.iclient.annotation.Insert;
import org.reactive.r2dbc.iclient.annotation.Select;
import org.reactive.r2dbc.iclient.annotation.Update;

/**
 * Enum util class to identify mapper method command type.
 * 
 * @author Bhautik Bhanani
 */
public enum SqlCommandType {
	UNKNOWN(null), SELECT(Select.class), INSERT(Insert.class), UPDATE(Update.class), DELETE(Delete.class), FLUSH(null);

	private final Class<? extends Annotation> annotation;

	private SqlCommandType(Class<? extends Annotation> annotation) {
		this.annotation = annotation;
	}

	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}

	public static SqlCommandType getType(Method method) {
		if (method.isAnnotationPresent(SELECT.getAnnotation())) {
			return SELECT;
		} else if (method.isAnnotationPresent(INSERT.getAnnotation())) {
			return INSERT;
		} else if (method.isAnnotationPresent(UPDATE.getAnnotation())) {
			return UPDATE;
		} else if (method.isAnnotationPresent(DELETE.getAnnotation())) {
			return DELETE;
		} else if (method.isAnnotationPresent(FLUSH.getAnnotation())) {
			return FLUSH;
		}
		return UNKNOWN;
	}
}
