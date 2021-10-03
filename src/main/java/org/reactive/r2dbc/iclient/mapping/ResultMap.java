package org.reactive.r2dbc.iclient.mapping;

import java.util.List;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.reactive.r2dbc.iclient.config.R2dbcConfiguration;
import org.reactive.r2dbc.iclient.exception.R2dbcInvalidTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

/**
 * Mapper class to hold mapping of element type with unique Id and class type.
 * 
 * @author Bhautik Bhanani
 */
public class ResultMap<T> {

	private R2dbcConfiguration configuration;
	private String id;
	private Class<T> type;
	private BiFunction<Row, RowMetadata, T> mapper;

	public ResultMap(R2dbcConfiguration configuration, String id, Class<T> type) {
		this.configuration = configuration;
		this.id = id;
		this.type = type;
	}

	public R2dbcConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(R2dbcConfiguration configuration) {
		this.configuration = configuration;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Class<T> getType() {
		return type;
	}

	public void setType(Class<T> type) {
		this.type = type;
	}

	public BiFunction<Row, RowMetadata, T> getMapper() {
		return mapper;
	}

	public void setMapper(BiFunction<Row, RowMetadata, T> mapper) {
		this.mapper = mapper;
	}

	/**
	 * Generates {@link ResultMap} from provided configurations.
	 * 
	 * @author Bhautik Bhanani
	 */
	public static class Builder<T> {

		private final static Logger log = LoggerFactory.getLogger(Builder.class);
		private ResultMap<T> resultMap;
		private List<ResultMapping> resultMappings;

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Builder(R2dbcConfiguration configuration, String id, Class<? extends Class> type,
				List<ResultMapping> resultMappings) {
			resultMap = new ResultMap(configuration, id, type);
			this.resultMappings = resultMappings;
		}

		public ResultMap<T> build() {

			resultMap.mapper = new BiFunction<Row, RowMetadata, T>() {

				@SuppressWarnings("deprecation")
				@Override
				public T apply(Row row, RowMetadata rowMetadata) {
					try {
						T instance = resultMap.type.newInstance();
						for (ResultMapping resultMapping : resultMappings) {
							Object value;
							if (resultMapping.getTypeConverter() != null) {
								value = resultMapping.getTypeConverter().execute(row, rowMetadata,
										resultMapping.getJavaType());
							} else if (StringUtils.isNotBlank(resultMapping.getResultMap())) {
								value = resultMap.configuration.getResultMap(resultMapping.getResultMap()).mapper
										.apply(row, rowMetadata);
							} else {
								value = row.get(resultMapping.getColumn(), resultMapping.getJavaType());
							}
							FieldUtils.writeField(instance, resultMapping.getProperty(), value, Boolean.TRUE);
						}
						return instance;
					} catch (InstantiationException | IllegalAccessException | R2dbcInvalidTypeException e) {
						log.error("Error creating ResultMap with id '{}': {}", resultMap.id, e);
					}
					return null;
				}
			};

			return resultMap;
		}
	}
}
