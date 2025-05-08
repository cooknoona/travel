package com.travel.exception.server;

/** HTTP 502 ERROR - External API ERROR */
public class BadGatewayException extends RuntimeException {
    public BadGatewayException(String message) {
        super(message);
    }
}
