package com.travel.exception.client;

/** HTTP 400 ERROR */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
