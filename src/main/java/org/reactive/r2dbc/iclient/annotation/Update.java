package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that specify an SQL for updating record(s).
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 * 	&#064;Update("UPDATE users SET name = :name WHERE id = :id")
 * 	boolean update(User user);
 * }
 * </pre>
 *
 * @author Bhautik Bhanani
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Update {
	/**
	 * Returns an SQL for updating record(s).
	 *
	 * @return an SQL for updating record(s)
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
