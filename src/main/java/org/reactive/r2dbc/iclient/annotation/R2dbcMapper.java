package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface for R2dbc iClient mappers.
 *
 * <p>
 * <b>How to use:</b>
 *
 * <pre>
 * &#064;R2dbcMapper
 * public interface UserMapper {
 * 	// ...
 * }
 * </pre>
 *
 * @author Bhautik Bhanani
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface R2dbcMapper {

}
