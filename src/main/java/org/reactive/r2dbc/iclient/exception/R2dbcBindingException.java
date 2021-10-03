package org.reactive.r2dbc.iclient.exception;

import io.r2dbc.spi.R2dbcException;

/**
 * @author Bhautik Bhanani
 */
public class R2dbcBindingException extends R2dbcException {

	private static final long serialVersionUID = -3847451444578576759L;

	public R2dbcBindingException() {
		super();
	}

	public R2dbcBindingException(String reason, String sqlState) {
		super(reason, sqlState);
	}

	public R2dbcBindingException(String reason, Throwable cause) {
		super(reason, cause);
	}

	public R2dbcBindingException(String reason) {
		super(reason);
	}

	public R2dbcBindingException(Throwable cause) {
		super(cause);
	}

}
