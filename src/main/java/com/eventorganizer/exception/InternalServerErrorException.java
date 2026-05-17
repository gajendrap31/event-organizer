package com.eventorganizer.exception;

public class InternalServerErrorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int statusCode = 500;

    public InternalServerErrorException(String message) {
        super(message);
    }

    public int getStatus() {
        return statusCode;
    }
}

