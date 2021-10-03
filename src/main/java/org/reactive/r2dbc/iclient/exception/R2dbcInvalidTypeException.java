package org.reactive.r2dbc.iclient.exception;

import io.r2dbc.spi.R2dbcException;

/**
 * @author Bhautik Bhanani
 */
public class R2dbcInvalidTypeException extends R2dbcException{

	private static final long serialVersionUID = -2771502328812367659L;

	public R2dbcInvalidTypeException() {
		super();
	}

	public R2dbcInvalidTypeException(String reason, String sqlState) {
		super(reason, sqlState);
	}

	public R2dbcInvalidTypeException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public R2dbcInvalidTypeException(String reason) {
		super(reason);
	}

	public R2dbcInvalidTypeException(Throwable cause) {
		super(cause);
	}
}
