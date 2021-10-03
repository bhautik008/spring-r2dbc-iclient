package org.reactive.r2dbc.iclient.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.reactive.r2dbc.iclient.annotation.R2dbcMapperScanner;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * A {@link ImportBeanDefinitionRegistrar} to allow annotation configuration of
 * R2dbc iClient mapper scanning. Mappers can be autowired via @Autowired
 * annotation.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcMapperScannerRegistery implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		AnnotationAttributes mapperScanAttrs = AnnotationAttributes
				.fromMap(importingClassMetadata.getAnnotationAttributes(R2dbcMapperScanner.class.getName()));
		if (mapperScanAttrs != null) {
			registerBeanDefinitions(importingClassMetadata, mapperScanAttrs, registry,
					generateBaseBeanName(importingClassMetadata, 0));
		}
	}

	void registerBeanDefinitions(AnnotationMetadata annoMeta, AnnotationAttributes annoAttrs,
			BeanDefinitionRegistry registry, String beanName) {

		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(R2dbcMapperScannerConfigurer.class);
		builder.addPropertyValue("processPropertyPlaceHolders", true);

		List<String> basePackages = new ArrayList<>();
		basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText)
				.collect(Collectors.toList()));

		basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText)
				.collect(Collectors.toList()));
		if (basePackages.isEmpty()) {
			basePackages.add(getDefaultBasePackage(annoMeta));
		}
		builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages));
		builder.addPropertyValue("defaultScope", ConfigurableBeanFactory.SCOPE_SINGLETON);

		registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {

	}

	private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
		return importingClassMetadata.getClassName() + "#" + R2dbcMapperScannerRegistery.class.getSimpleName() + "#"
				+ index;
	}

	private static String getDefaultBasePackage(AnnotationMetadata importingClassMetadata) {
		return ClassUtils.getPackageName(importingClassMetadata.getClassName());
	}
}
