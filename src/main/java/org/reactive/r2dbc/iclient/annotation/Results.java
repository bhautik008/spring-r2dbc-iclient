package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that be grouping mapping definitions for property.
 * 
 * <p>
 * <b>How to use:</b>
 * 
 * <pre>
 * public interface UserMapper {
 * 	&#064;Results(id = "userMap", type = User.class, value = {
 * 			&#064;Result(property = "id", column = "id", id = true, javaType = Integer.class),
 * 			&#064;Result(property = "name", column = "name", javaType = String.class),,
 * 			&#064;Result(property = "fullAddress", javaType = String.class, typeConverter = FullAddressConverter.class ),
 * 			&#064;Result(property = "userAddress", javaType = UserAddress.class, resultMap = "userAddressMap") })
 * 	&#064;Select("SELECT * FROM users WHERE id = :id")
 * 	Mono<User> selectById(int id);
 * }
 * </pre>
 * 
 * @author Bhautik Bhanani
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Results {
	/**
	 * Returns the id of this result map.
	 *
	 * @return the id of this result map
	 */
	String id() default "";

	/**
	 * Class type of this result map
	 * 
	 * @return class type of this reult map
	 */
	Class<?> type() default Class.class;

	/**
	 * Returns mapping definitions for property.
	 *
	 * @return mapping definitions
	 */
	Result[] value() default {};
}
