package com.eventorganizer.exception;

public class BadRequestException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final int statusCode = 400;

	public BadRequestException(String message) {
		super(message);
	}
	
	public int getStatus() {
		return statusCode;
	}

}
