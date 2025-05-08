package com.travel.exception.client;

/** HTTP 403 ERROR - No Authority (ADMIN)*/
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
