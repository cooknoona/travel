package com.travel.exception.client;

/** HTTP 409 ERROR - Duplicated data, crashed data */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
