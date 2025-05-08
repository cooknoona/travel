package com.travel.exception.client;

/** HTTP 401 ERROR - Authentication needs, No Token, Token issue problems */
public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
