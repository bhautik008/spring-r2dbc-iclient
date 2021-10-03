package org.reactive.r2dbc.iclient.mapper;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * A {@link ClassPathBeanDefinitionScanner} that registers Mappers by {@code basePackage}.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcClassPathMapperScanner extends ClassPathBeanDefinitionScanner {

	private final static Logger log = LoggerFactory.getLogger(R2dbcClassPathMapperScanner.class);
	static final String FACTORY_BEAN_OBJECT_TYPE = "factoryBeanObjectType";
	private Class<? extends Annotation> annotationClass;
	private ApplicationContext applicationContext;
	private String defaultScope;

	public R2dbcClassPathMapperScanner(BeanDefinitionRegistry registry) {
		super(registry);
	}

	public Class<? extends Annotation> getAnnotationClass() {
		return annotationClass;
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	public String getDefaultScope() {
		return defaultScope;
	}

	public void setDefaultScope(String defaultScope) {
		this.defaultScope = defaultScope;
	}

	public void registerFilters() {
		boolean acceptAllInterfaces = true;

		// if specified, use the given annotation and / or marker interface
		if (this.annotationClass != null) {
			addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
			acceptAllInterfaces = false;
		}

		if (acceptAllInterfaces) {
			// default include filter that accepts all classes
			addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
		}

		// exclude package-info.java
		addExcludeFilter((metadataReader, metadataReaderFactory) -> {
			String className = metadataReader.getClassMetadata().getClassName();
			return className.endsWith("package-info");
		});
	}

	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

		if (beanDefinitions.isEmpty()) {
			log.warn("No MyBatis mapper was found in '{}' package. Please check your configuration.",
					Arrays.toString(basePackages));
		} else {
			processBeanDefinitions(beanDefinitions);
		}

		return beanDefinitions;
	}

	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			log.warn("Skipping MapperFactoryBean with name '" + beanName + "' and '" + beanDefinition.getBeanClassName()
					+ "' mapperInterface" + ". Bean already defined with the same name!");
			return false;
		}
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		AbstractBeanDefinition definition;
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (AbstractBeanDefinition) holder.getBeanDefinition();

			String beanClassName = definition.getBeanClassName();
			log.debug("Creating R2dbcMapperBean with name '" + holder.getBeanName() + "' and '"
					+ beanClassName + "' mapperInterface");
			
			definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
			definition.setBeanClass(R2dbcMapperBeanFactory.class);
			definition.setAttribute(FACTORY_BEAN_OBJECT_TYPE, beanClassName);
			log.debug("Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
	        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
	        definition.setScope(defaultScope);
		}
	}
}
