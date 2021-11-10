package org.reactive.r2dbc.iclient.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.reactive.r2dbc.iclient.annotation.Delete;
import org.reactive.r2dbc.iclient.annotation.Insert;
import org.reactive.r2dbc.iclient.annotation.PropertyMapper;
import org.reactive.r2dbc.iclient.annotation.ResultMap;
import org.reactive.r2dbc.iclient.annotation.Results;
import org.reactive.r2dbc.iclient.annotation.Select;
import org.reactive.r2dbc.iclient.annotation.Update;
import org.reactive.r2dbc.iclient.core.R2dbcSqlSession;
import org.reactive.r2dbc.iclient.exception.R2dbcBindingException;
import org.reactive.r2dbc.iclient.exception.R2dbcQueryException;
import org.reactive.r2dbc.iclient.reflection.ParameterResolver;
import org.reactive.r2dbc.iclient.reflection.PropertyTokenizer;
import org.reactive.r2dbc.iclient.type.SqlCommandType;
import org.reactive.r2dbc.iclient.util.NamedParameterUtils;
import org.reactive.r2dbc.iclient.util.ParsedSql;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Holds mapper's method information such as arguments, sql command type and sql
 * query statement.
 * 
 * It executes a statement by calling {@link R2dbcSqlSession} method upon mapper
 * method execution.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcMapperMethod {

	private final SqlCommand command;
	private final MethodSignature method;

	public R2dbcMapperMethod(Class<?> mapperInterface, Method method) {
		this.method = new MethodSignature(mapperInterface, method);
		this.command = new SqlCommand(method);
	}

	public SqlCommand getCommand() {
		return command;
	}

	public MethodSignature getMethod() {
		return method;
	}

	public Object execute(R2dbcSqlSession session, Object[] args) {
		Object result = null;
		switch (command.getType()) {
		case SELECT: {
			Object param = this.method.getNamedParameter(args);
			if (method.isReturnsVoid()) {
				session.select(this, param);
			} else if (method.isReturnsMany()) {
				result = session.select(this, param);
			} else {
				result = session.selectOne(this, param);
			}
			break;
		}
		case INSERT: {
			Object param = this.method.getNamedParameter(args);
			result = session.insert(this, param);
			break;
		}
		case UPDATE: {
			Object param = this.method.getNamedParameter(args);
			result = session.update(this, param);
			break;
		}
		case DELETE: {
			Object param = this.method.getNamedParameter(args);
			result = session.delete(this, param);
			break;
		}
		default:
			throw new R2dbcQueryException("Unknown execution method for: " + command.getName());
		}
		if (result == null && method.getReturnType().isPrimitive() && !method.isReturnsVoid()) {
			throw new R2dbcQueryException("Mapper method '" + method.getName() + "' "
					+ "attempted to return null from a method with a primitive return type (" + method.getReturnType()
					+ ").");
		}
		return result;
	}

	/**
	 * Holds mapper's method information about arguments names, values, return type
	 * and result map.
	 * 
	 * @author Bhautik Bhanani
	 */
	public static class MethodSignature {
		private final String name;
		private final boolean returnsMany;
		private final boolean returnsVoid;
		private final Class<?> returnType;
		private final Class<?> returnInferredType;
		private final ParameterResolver paramNameResolver;
		private String resultMap;

		public MethodSignature(Class<?> mapperInterface, Method method) {
			name = method.getName();
			if (method.getReturnType().equals(Flux.class)) {
				returnType = Flux.class;
				returnsMany = true;
			} else {
				returnType = Mono.class;
				returnsMany = false;
			}
			this.returnInferredType = parseInferredClass(method.getGenericReturnType());
			this.returnsVoid = this.returnInferredType.equals(Void.TYPE);
			this.paramNameResolver = new ParameterResolver(method);
			if (method.isAnnotationPresent(Select.class)
					&& StringUtils.isNotBlank(method.getAnnotation(Select.class).resultMap())) {
				resultMap = method.getAnnotation(Select.class).resultMap();
			} else if (method.isAnnotationPresent(Results.List.class)) {
				resultMap = method.getAnnotation(Results.List.class).value()[0].id();
			} else if (method.isAnnotationPresent(Results.class)) {
				resultMap = method.getAnnotation(Results.class).id();
			} else if (method.isAnnotationPresent(ResultMap.class)) {
				resultMap = method.getAnnotation(ResultMap.class).value()[0];
			}
		}

		public String getResultMap() {
			return resultMap;
		}

		public void setResultMap(String resultMap) {
			this.resultMap = resultMap;
		}

		public String getName() {
			return name;
		}

		public boolean isReturnsMany() {
			return returnsMany;
		}

		public boolean isReturnsVoid() {
			return returnsVoid;
		}

		public Class<?> getReturnType() {
			return returnType;
		}

		public Class<?> getReturnInferredType() {
			return returnInferredType;
		}

		public ParameterResolver getParamNameResolver() {
			return paramNameResolver;
		}

		public Object getNamedParameter(Object[] args) {
			return this.paramNameResolver.getNamedParameter(args);
		}

		public static Class<?> parseInferredClass(Type genericType) {
			Class<?> inferredClass = null;
			if (genericType instanceof ParameterizedType) {
				ParameterizedType type = (ParameterizedType) genericType;
				Type[] typeArguments = type.getActualTypeArguments();
				if (typeArguments.length > 0) {
					final Type typeArgument = typeArguments[0];
					if (typeArgument instanceof ParameterizedType) {
						inferredClass = (Class<?>) ((ParameterizedType) typeArgument).getActualTypeArguments()[0];
					} else if (typeArgument instanceof Class) {
						inferredClass = (Class<?>) typeArgument;
					} else {
						String typeName = typeArgument.getTypeName();
						if (typeName.contains(" ")) {
							typeName = typeName.substring(typeName.lastIndexOf(" ") + 1);
						}
						if (typeName.contains("<")) {
							typeName = typeName.substring(0, typeName.indexOf("<"));
						}
						try {
							inferredClass = Class.forName(typeName);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if (inferredClass == null && genericType instanceof Class) {
				inferredClass = (Class<?>) genericType;
			}
			return inferredClass;
		}
	}

	/**
	 * Holds mapper's method information about sql query statement and property to
	 * parse in a query.
	 * 
	 * @author Bhautik Bhanani
	 */
	public static class SqlCommand {

		private final String name;
		private final String statement;
		private final SqlCommandType type;
		private boolean retrieveId;
		private String idColumn;
		private Class<?> idType;
		private Map<String, PropertyTokenizer> propertyMapper = new HashMap<>();
		private Map<String, Class<?>> propertyClass = new HashMap<>();

		public SqlCommand(Method method) {
			type = SqlCommandType.getType(method);
			name = type.name();
			PropertyMapper[] propertyList;
			switch (type) {
			case SELECT:
				statement = ((Select) method.getAnnotation(type.getAnnotation())).value();
				propertyList = ((Select) method.getAnnotation(type.getAnnotation())).propertyMapper();
				break;
			case INSERT:
				statement = ((Insert) method.getAnnotation(type.getAnnotation())).value();
				idColumn = ((Insert) method.getAnnotation(type.getAnnotation())).retrieveId();
				idType = ((Insert) method.getAnnotation(type.getAnnotation())).idType();
				if (StringUtils.isNotBlank(idColumn)) {
					retrieveId = Boolean.TRUE;
				}
				propertyList = ((Insert) method.getAnnotation(type.getAnnotation())).propertyMapper();
				break;
			case UPDATE:
				statement = ((Update) method.getAnnotation(type.getAnnotation())).value();
				propertyList = ((Update) method.getAnnotation(type.getAnnotation())).propertyMapper();
				break;
			case DELETE:
				statement = ((Delete) method.getAnnotation(type.getAnnotation())).value();
				propertyList = ((Delete) method.getAnnotation(type.getAnnotation())).propertyMapper();
				break;
			default:
				throw new R2dbcQueryException("Statement is required for command: ", name);
			}
			parseSql();
			parsePropertyClass(propertyList);
		}

		public boolean isRetrieveId() {
			return retrieveId;
		}

		public void setRetrieveId(boolean retrieveId) {
			this.retrieveId = retrieveId;
		}

		public String getIdColumn() {
			return idColumn;
		}

		public void setIdColumn(String idColumn) {
			this.idColumn = idColumn;
		}

		public Class<?> getIdType() {
			return idType;
		}

		public void setIdType(Class<?> idType) {
			this.idType = idType;
		}

		public Map<String, PropertyTokenizer> getPropertyMapper() {
			return propertyMapper;
		}

		public void setPropertyMapper(Map<String, PropertyTokenizer> propertyMapper) {
			this.propertyMapper = propertyMapper;
		}

		public String getName() {
			return name;
		}

		public String getStatement() {
			return statement;
		}

		public SqlCommandType getType() {
			return type;
		}

		public Class<?> getPropertyClass(String propertyName) {
			return Optional.ofNullable(propertyClass.get(propertyName))
					.orElseThrow(() -> new R2dbcBindingException("Class defination require for null property '"
							+ propertyName + "'. Please specify property and class mapping."));
		}

		private void parseSql() {
			ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(statement);
			for (String param : parsedSql.getParameterNames()) {
				PropertyTokenizer property = new PropertyTokenizer(param);
				propertyMapper.put(property.getOriginalProperty(), property);
			}
		}

		private void parsePropertyClass(PropertyMapper[] propertyList) {
			if (propertyList != null && propertyList.length > 0) {
				for (PropertyMapper property : propertyList) {
					Stream.of(property.properties()).forEach(s -> propertyClass.put(s, property.javaType()));
				}
			}
		}
	}
}
