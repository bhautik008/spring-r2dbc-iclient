package org.reactive.r2dbc.iclient.exception;

import io.r2dbc.spi.R2dbcException;

/**
 * @author Bhautik Bhanani
 */
public class R2dbcQueryException extends R2dbcException {

	private static final long serialVersionUID = -1948101985112005157L;

	public R2dbcQueryException() {
		super();
	}

	public R2dbcQueryException(String reason, String sqlState) {
		super(reason, sqlState);
	}

	public R2dbcQueryException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public R2dbcQueryException(String reason) {
		super(reason);
	}

	public R2dbcQueryException(Throwable cause) {
		super(cause);
	}

}
