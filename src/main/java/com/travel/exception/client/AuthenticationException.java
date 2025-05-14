package com.travel.exception.client;

/** HTTP 401 ERROR - Used for Spring Security Filter Chain, Not for Global Exception Handler */
public class AuthenticationException extends org.springframework.security.core.AuthenticationException {
    public AuthenticationException(String message) {
        super(message);
    }
}
