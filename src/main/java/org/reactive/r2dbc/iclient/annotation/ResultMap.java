package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that specify result map names to use.
 * 
 * <p>
 * <b>How to use:</b><br>
 * Mapper interface:
 *
 * <pre>
 * public interface UserMapper {
 * 	&#064;Select("SELECT id, name FROM users WHERE id = :id")
 * 	&#064;ResultMap("userMap")
 * 	Mono<User> selectById(int id);
 *
 * 	&#064;Select("SELECT u.id, u.name FROM users u INNER JOIN users_email ue ON u.id = ue.id WHERE ue.email = :email")
 * 	&#064;ResultMap("userMap")
 * 	Flux<User> selectByEmail(String email);
 * }
 * </pre>
 * 
 * @author Bhautik Bhanani
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ResultMap {
	/**
	 * Returns result map names to use.
	 *
	 * @return result map names
	 */
	String[] value();
}