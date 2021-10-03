package org.reactive.r2dbc.iclient.mapping;

import org.reactive.r2dbc.iclient.proxy.R2dbcMapperMethod;
import org.reactive.r2dbc.iclient.reflection.ParamObject;
import org.reactive.r2dbc.iclient.reflection.PropertyTokenizer;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.DatabaseClient.GenericExecuteSpec;

/**
 * PreparedStatement to generate SQL statement binding parameters.
 * 
 * @author Bhautik Bhanani
 */
public class R2dbcPreparedStatement {

	private GenericExecuteSpec spec;
	private final R2dbcMapperMethod method;

	public R2dbcPreparedStatement(DatabaseClient databaseClient, R2dbcMapperMethod method) {
		this.spec = databaseClient.sql(method.getCommand().getStatement());
		this.method = method;
	}

	public GenericExecuteSpec getExecuteSpec() {
		return spec;
	}

	public GenericExecuteSpec parameterize(Object param) {
		if (!method.getCommand().getPropertyMapper().isEmpty()) {
			for (PropertyTokenizer property : method.getCommand().getPropertyMapper().values()) {
				if (param == null) {
					spec = spec.bindNull(property.getOriginalProperty(),
							method.getCommand().getPropertyClass(property.getOriginalProperty()));
				} else {
					ParamObject paramObject = ParamObject.forObject(param);
					Object value = paramObject.getValue(property);
					if (value == null) {
						spec = spec.bindNull(property.getOriginalProperty(),
								method.getCommand().getPropertyClass(property.getOriginalProperty()));
					} else {
						spec = spec.bind(property.getOriginalProperty(), value);
					}
				}
			}
		}
		return spec;
	}
}
