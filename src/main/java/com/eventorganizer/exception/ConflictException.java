package com.eventorganizer.exception;

public class ConflictException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	private final int statusCode = 409;

	public ConflictException(String message) {
		super(message);
	}
	
	public int getStatus() {
		return statusCode;
	}

}

