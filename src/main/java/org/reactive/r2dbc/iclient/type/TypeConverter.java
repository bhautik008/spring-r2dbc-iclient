package org.reactive.r2dbc.iclient.type;

import org.reactive.r2dbc.iclient.exception.R2dbcInvalidTypeException;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

/**
 * Converter to convert a database result into single object.
 * 
 * <p>
 * <b>How to use:</b>
 * 
 * <pre>
 * public class FullAddressConverter implements TypeConverter {
 * 	&#064;Override
 * 	public Object convert(Row row, RowMetadata rowMetadata) {
 * 		String address = row.get("userAddress", String.class);
 * 		String city = row.get("userCity", String.class);
 * 		String state = row.get("userState", String.class);
 * 		String zip = row.get("userZip", String.class);
 * 
 * 		return address + ", " + city + ", " + state + " - " + zip;
 * 	}
 * }
 * </pre>
 * <p>
 * 
 * <pre>
 * public interface UserMapper {
 * 	&#064;Results(id = "userMap", type = User.class, value = {
 * 			&#064;Result(property = "fullAddress", javaType = String.class, typeConverter = FullAddressConverter.class) })
 * 	&#064;Select("SELECT * FROM users WHERE id = :id")
 * 	Mono<User> selectById(int id);
 * }
 * </pre>
 * 
 * @author Bhautik Bhanani
 * 
 * @see io.r2dbc.spi.Row;
 */
public interface TypeConverter {

	public Object convert(Row row, RowMetadata rowMetadata);

	default Object execute(Row row, RowMetadata rowMetadata, Class<?> type) {
		Object result = convert(row, rowMetadata);
		if (!validate(result, type)) {
			throw new R2dbcInvalidTypeException("Invalid result type. Result type must be " + type.getName());
		}
		return result;
	}

	default boolean validate(Object result, Class<?> type) {
		return type.isInstance(result);
	}
}
