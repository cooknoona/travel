package com.travel.exception.client;

/** HTTP 422 ERROR - Unavailable parameters */
public class UnprocessableEntityException extends RuntimeException {
    public UnprocessableEntityException(String message) {
        super(message);
    }
}
