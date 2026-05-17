package com.eventorganizer.exception;

public class UnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode = 401;

    public UnauthorizedException(String message) {
        super(message);
    }

    public int getStatus() {
        return statusCode;
    }
}

