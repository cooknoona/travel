package com.travel.exception.server;

/** HTTP 500 ERROR */
public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}
