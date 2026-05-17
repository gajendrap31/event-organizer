package com.eventorganizer.exception;

public class ForbiddenException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode = 403;

    public ForbiddenException(String message) {
        super(message);
    }

    public int getStatus() {
        return statusCode;
    }
}

