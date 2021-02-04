package org.onap.ransim.rest.api.exceptions;

public class RansimException extends Throwable {

	public RansimException() {
		super();
	}

	public RansimException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RansimException(String message, Throwable cause) {
		super(message, cause);
	}

	public RansimException(String message) {
		super(message);
	}

	public RansimException(Throwable cause) {
		super(cause);
	}
	
	

}
