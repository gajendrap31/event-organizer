package com.eventorganizer.exception;

public class TooManyRequestsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode = 429;

    public TooManyRequestsException(String message) {
        super(message);
    }

    public int getStatus() {
        return statusCode;
    }
}