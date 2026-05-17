package com.eventorganizer.exception;

public class AlreadySolvedException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public AlreadySolvedException(String message) {
		super(message);
	}
}