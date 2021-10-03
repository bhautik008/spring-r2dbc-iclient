package org.reactive.r2dbc.iclient.exception;

import io.r2dbc.spi.R2dbcException;

/**
 * @author Bhautik Bhanani
 */
public class R2dbcReflectionException extends R2dbcException {

	private static final long serialVersionUID = 6145471450316907225L;

	public R2dbcReflectionException() {
		super();
	}

	public R2dbcReflectionException(String reason, String sqlState) {
		super(reason, sqlState);
	}

	public R2dbcReflectionException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public R2dbcReflectionException(String reason) {
		super(reason);
	}

	public R2dbcReflectionException(Throwable cause) {
		super(cause);
	}
}
