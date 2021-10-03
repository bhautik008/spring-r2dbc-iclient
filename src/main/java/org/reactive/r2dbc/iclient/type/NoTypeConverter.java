package org.reactive.r2dbc.iclient.type;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;

/**
 * Default implementation of {@link TypeConverter}.
 * <p>
 * Note: Internal use only.
 * 
 * @author Bhautik Bhanani
 */
public class NoTypeConverter implements TypeConverter {

	@Override
	public Object convert(Row row, RowMetadata rowMetadata) {
		return null;
	}

}
