package com.eventorganizer.exception;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode = 404;

    public NotFoundException(String message) {
        super(message);
    }

    public int getStatus() {
        return statusCode;
    }
}

