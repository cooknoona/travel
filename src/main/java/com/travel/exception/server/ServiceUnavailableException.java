package com.travel.exception.server;

/** HTTP 503 ERROR - Server not in service */
public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(String message) {
        super(message);
    }
}
