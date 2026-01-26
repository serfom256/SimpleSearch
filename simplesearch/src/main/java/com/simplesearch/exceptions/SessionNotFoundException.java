package com.simplesearch.exceptions;

public class SessionNotFoundException extends RuntimeException {
    public SessionNotFoundException(String id) {
        super(String.format("Session with the specified id: %s not", id));
    }
}
