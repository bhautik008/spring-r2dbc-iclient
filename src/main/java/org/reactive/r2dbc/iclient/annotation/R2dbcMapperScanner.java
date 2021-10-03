package org.reactive.r2dbc.iclient.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.reactive.r2dbc.iclient.mapper.R2dbcMapperScannerRegistery;
import org.springframework.context.annotation.Import;

/**
 * Use this annotation to register R2dbc mapper interfaces when using Java
 * Config.
 * 
 * <p>
 * {@link #basePackages} (or its alias {@link #value}) may be specified to
 * define specific packages to scan. If specific packages are not defined,
 * scanning will occur from the package of the class that declares this
 * annotation.
 * 
 * <p>
 * Configuration example:
 * </p>
 * 
 * <pre class="code">
 * &#064;Configuration
 * &#064;R2dbcMapperScanner("com.example.mappers")
 * public class AppConfig {
 * 
 * }
 * </pre>
 * 
 * @author Bhautik Bhanani
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(R2dbcMapperScannerRegistery.class)
public @interface R2dbcMapperScanner {

	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise
	 * annotation declarations e.g.: {@code @@R2dbcMapperScanner("org.my.pkg")}
	 * instead of {@code @R2dbcMapperScanner(basePackages = "org.my.pkg"})}.
	 *
	 * @return base package names
	 */
	String[] value() default {};

	/**
	 * Base packages to scan for R2dbc mappers/repositories. Note that only
	 * interfaces with at least one method will be registered; concrete classes will
	 * be ignored.
	 *
	 * @return base package names for scanning mapper interface
	 */
	String[] basePackages() default {};

}
