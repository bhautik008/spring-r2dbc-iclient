package org.reactive.r2dbc.iclient.mapper;

import static org.springframework.util.Assert.notNull;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * BeanDefinitionRegistryPostProcessor that searches recursively starting from a
 * base package for interfaces and registers them as
 * {@code R2dbcMapperBeanFactory}. Note that only interfaces with at least one
 * method will be registered; concrete classes will be ignored.
 * <p>
 * The {@code basePackage} property can contain more than one package name,
 * separated by either commas or semicolons.
 * <p>
 * This configurer enables autowire for all the beans that it creates so that
 * they are automatically autowired with the proper
 * {@link R2dbcSqlSessionFactory}.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcMapperScannerConfigurer
		implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

	private String basePackage;
	private Class<? extends Annotation> annotationClass;
	private ApplicationContext applicationContext;
	private String beanName;
	private boolean processPropertyPlaceHolders;
	private BeanNameGenerator nameGenerator;
	private String defaultScope;

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return annotationClass;
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public boolean isProcessPropertyPlaceHolders() {
		return processPropertyPlaceHolders;
	}

	public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {
		this.processPropertyPlaceHolders = processPropertyPlaceHolders;
	}

	public BeanNameGenerator getNameGenerator() {
		return nameGenerator;
	}

	public void setNameGenerator(BeanNameGenerator nameGenerator) {
		this.nameGenerator = nameGenerator;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public String getDefaultScope() {
		return defaultScope;
	}

	public void setDefaultScope(String defaultScope) {
		this.defaultScope = defaultScope;
	}

	public String getBeanName() {
		return beanName;
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(this.basePackage, "Property 'basePackage' is required");
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		if (this.processPropertyPlaceHolders) {
			processPropertyPlaceHolders();
		}

		R2dbcClassPathMapperScanner scanner = new R2dbcClassPathMapperScanner(registry);
		scanner.setAnnotationClass(annotationClass);
		scanner.setApplicationContext(applicationContext);
		scanner.setResourceLoader(applicationContext);
		scanner.setDefaultScope(defaultScope);
		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	private void processPropertyPlaceHolders() {
		Map<String, PropertyResourceConfigurer> prcs = applicationContext
				.getBeansOfType(PropertyResourceConfigurer.class, false, false);

		if (!prcs.isEmpty() && applicationContext instanceof ConfigurableApplicationContext) {
			BeanDefinition mapperScannerBean = ((ConfigurableApplicationContext) applicationContext).getBeanFactory()
					.getBeanDefinition(beanName);

			// PropertyResourceConfigurer does not expose any methods to explicitly perform
			// property placeholder substitution. Instead, create a BeanFactory that just
			// contains this mapper scanner and post process the factory.
			DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
			factory.registerBeanDefinition(beanName, mapperScannerBean);

			for (PropertyResourceConfigurer prc : prcs.values()) {
				prc.postProcessBeanFactory(factory);
			}

			PropertyValues values = mapperScannerBean.getPropertyValues();

			this.basePackage = getPropertyValue("basePackage", values);
			this.defaultScope = getPropertyValue("defaultScope", values);
		}
		this.basePackage = Optional.ofNullable(this.basePackage).map(getEnvironment()::resolvePlaceholders)
				.orElse(null);
		this.defaultScope = Optional.ofNullable(this.defaultScope).map(getEnvironment()::resolvePlaceholders)
				.orElse(null);
	}

	private Environment getEnvironment() {
		return this.applicationContext.getEnvironment();
	}

	private String getPropertyValue(String propertyName, PropertyValues values) {
		PropertyValue property = values.getPropertyValue(propertyName);

		if (property == null) {
			return null;
		}

		Object value = property.getValue();

		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return value.toString();
		} else if (value instanceof TypedStringValue) {
			return ((TypedStringValue) value).getValue();
		} else {
			return null;
		}
	}
}
