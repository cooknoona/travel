package com.travel.exception.client;

/** HTTP 404 ERROR - Entity not found or has no data */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
