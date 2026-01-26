package com.simplesearch.exceptions;

public class PathNotFoundException extends RuntimeException {

    public PathNotFoundException(String file) {
        super(String.format("Path or file %s not found", file));
    }
}
