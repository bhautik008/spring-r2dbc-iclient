package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that specify an SQL for deleting record(s).
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 * 	&#064;Delete("DELETE FROM users WHERE id = :id")
 * 	Mono<Integer> deleteById(int id);
 * }
 * </pre>
 * 
 * @author Bhautik Bhanani
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Delete {
	/**
	 * Returns an SQL for deleting record(s).
	 *
	 * @return an SQL for deleting record(s)
	 */
	String value();

	/**
	 * PropertyMapper to map property used in query with Java class type
	 * 
	 * Require a property mapping in case any property passed null. Please refer
	 * this document for further information: <a href=
	 * "https://docs.spring.io/spring-data/r2dbc/docs/current/api/org/springframework/data/r2dbc/core/DatabaseClient.BindSpec.html#bindNull-java.lang.String-java.lang.Class-">https://docs.spring.io/spring-data/r2dbc/docs/current/api/org/springframework/data/r2dbc/core/DatabaseClient.BindSpec.html#bindNull-java.lang.String-java.lang.Class-</a>
	 * 
	 * @return a list of @PropertyMapper
	 */
	PropertyMapper[] propertyMapper() default {};
}
