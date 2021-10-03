package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.reactive.r2dbc.iclient.type.NoTypeConverter;
import org.reactive.r2dbc.iclient.type.TypeConverter;

/**
 * The annotation that specify a mapping definition for the property.
 *
 * @see Results
 * @author Bhautik Bhanani
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(Results.class)
public @interface Result {
	/**
	 * Returns whether id column or not.
	 *
	 * @return {@code true} if id column; {@code false} if otherwise
	 */
	boolean id() default false;

	/**
	 * Return the column name(or column label) to map to this argument.
	 *
	 * @return the column name(or column label)
	 */
	String column() default "";

	/**
	 * Returns the property name for applying this mapping.
	 *
	 * @return the property name
	 */
	String property() default "";

	/**
	 * Return the java type for this argument.
	 *
	 * @return the java type
	 */
	Class<?> javaType() default void.class;

	/**
	 * Returns the {@link TypeConverter} type for retrieving a column value from
	 * result set.
	 * 
	 * @return the {@link TypeConverter} type
	 */
	Class<? extends TypeConverter> typeConverter() default NoTypeConverter.class;

	/**
	 * Returns the ID of resultMap to map data to this property
	 * 
	 * @return the resultMap ID
	 */
	String resultMap() default "";
}
