package com.serliunx.statemanagement.exception;

/**
 * 状态机异常
 *
 * @author <a href="mailto:serliunx@yeah.net">SerLiunx</a>
 * @version 1.0.0
 * @since 2024/12/28
 */
public class StateException extends RuntimeException {

	public StateException() {
	}

	public StateException(String message) {
		super(message);
	}

	public StateException(String message, Throwable cause) {
		super(message, cause);
	}

	public StateException(Throwable cause) {
		super(cause);
	}

	public StateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
