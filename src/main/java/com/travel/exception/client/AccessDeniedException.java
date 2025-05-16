package com.travel.exception.client;

/** HTTP 403 ERROR - No Authority (ADMIN)*/
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
