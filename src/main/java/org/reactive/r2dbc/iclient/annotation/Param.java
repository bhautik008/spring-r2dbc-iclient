package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that specify the parameter name.
 * @Param annotation is required if query is asking for parameters.
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * public interface UserMapper {
 *   &#064;Select("SELECT id, name FROM users WHERE name = :name")
 *   User selectById(&#064;Param("name") String value);
 * }
 * </pre>
 *
 * @author Bhautik Bhanani
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
  /**
   * Returns the parameter name.
   *
   * @return the parameter name
   */
  String value();
}