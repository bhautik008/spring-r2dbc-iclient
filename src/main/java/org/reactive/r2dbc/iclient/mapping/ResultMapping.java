package org.reactive.r2dbc.iclient.mapping;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.isTrue;

import java.lang.reflect.Field;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.reactive.r2dbc.iclient.type.NoTypeConverter;
import org.reactive.r2dbc.iclient.type.TypeConverter;

/**
 * Mapping of database column to java element field.
 * 
 * @author Bhautik Bhanani
 */
public class ResultMapping {

	private String property;
	private String column;
	private Class<?> javaType;
	private TypeConverter typeConverter;
	private String resultMap;

	public ResultMapping(String property, Class<?> javaType) {
		this.property = property;
		this.javaType = javaType;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Class<?> getJavaType() {
		return javaType;
	}

	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}

	public TypeConverter getTypeConverter() {
		return typeConverter;
	}

	public void setTypeConverter(TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
	}

	public String getResultMap() {
		return resultMap;
	}

	public void setResultMap(String resultMap) {
		this.resultMap = resultMap;
	}

	@SuppressWarnings("deprecation")
	public static ResultMapping build(Class<?> clazz, String property, String column, Class<?> javaType,
			Class<? extends TypeConverter> typeConverter, String resultMap) {

		notNull(property, "Property field is required for @Result");
		notNull(javaType, "JavaType is required for @Result");

		Field field = FieldUtils.getField(clazz, property, Boolean.TRUE);
		isTrue(field != null, "Property '" + property + "' not found in class '" + clazz.getName() + "'");
		isTrue(field.getType().equals(javaType), "Property '" + property + "' type is not '" + javaType.getName()
				+ "' in class '" + clazz.getName() + "'");

		ResultMapping rm = new ResultMapping(property, javaType);

		if (typeConverter != null && !typeConverter.equals(NoTypeConverter.class)) {
			try {
				rm.setTypeConverter(typeConverter.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				notNull(rm.getTypeConverter(), "Error while initializing TypeConverter of: " + typeConverter.getName());
			}
		} else {
			notNull(column, "Column field is required for @Result");
			rm.setColumn(column);
		}

		rm.setResultMap(resultMap);

		return rm;
	}
}
