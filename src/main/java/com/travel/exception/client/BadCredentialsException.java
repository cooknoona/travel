package com.travel.exception.client;

/** HTTP 401 ERROR - Invalid userId, password */
public class BadCredentialsException extends RuntimeException {
  public BadCredentialsException(String message) {
    super(message);
  }
}
