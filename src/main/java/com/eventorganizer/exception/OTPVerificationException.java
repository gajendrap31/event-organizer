package com.eventorganizer.exception;

public class OTPVerificationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OTPVerificationException(String message) {
        super(message);
    }
}
