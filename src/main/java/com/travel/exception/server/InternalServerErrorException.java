package com.travel.exception.server;

/** HTTP 500 ERROR */
public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
