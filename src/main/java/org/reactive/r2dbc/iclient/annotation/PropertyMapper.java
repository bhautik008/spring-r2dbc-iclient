package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that maps properties used in query with Java class type.
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 * 	&#064;Select(value = "SELECT id, name FROM users WHERE id = :id", propertyMapper = {
 *    &#064;PropertyMapper(javaType = Integer.class, properties = ["id"])
 *  })
 * 	Mono<User> selectById(int id);
 * }
 * </pre>
 *
 * @author Bhautik Bhanani
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(PropertyMapper.List.class)
public @interface PropertyMapper {

	/**
	 * Return the java type for this argument.
	 *
	 * @return the java type
	 */
	Class<?> javaType() default Class.class;

	/**
	 * Returns the property name(s) for applying this java class mapping.
	 *
	 * @return the property name(s)
	 */
	String[] properties() default "";

	/**
	 * The container annotation for {@link PropertyMapper}
	 * 
	 * @author Bhautik Bhanani
	 *
	 */
	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface List {
		PropertyMapper[] value();
	}
}
