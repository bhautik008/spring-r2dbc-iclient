package org.reactive.r2dbc.iclient.exception;

import io.r2dbc.spi.R2dbcException;

public class R2dbcConnectionException extends R2dbcException {

	private static final long serialVersionUID = 3610465837562820897L;

	public R2dbcConnectionException() {
		super();
	}

	public R2dbcConnectionException(String reason) {
		super(reason);
	}

	public R2dbcConnectionException(String reason, Throwable throwable) {
		super(reason, throwable);
	}

	public R2dbcConnectionException(Throwable throwable) {
		super(throwable);
	}
}
