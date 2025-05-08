package com.travel.exception.client;

/** HTTP 405 ERROR - No longer support HTTP method */
public class MethodNotAllowedException extends RuntimeException {
    public MethodNotAllowedException(String message) {
        super(message);
    }
}
