package com.travel.exception.client;

/** HTTP 423 ERROR - An account or a post has locked either permanently or temporarily */
public class LockedException extends RuntimeException {
    public LockedException(String message) {
        super(message);
    }
}
