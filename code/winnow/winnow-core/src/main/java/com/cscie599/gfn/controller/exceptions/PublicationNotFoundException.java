package com.cscie599.gfn.controller.exceptions;

public class PublicationNotFoundException extends RuntimeException {
    public PublicationNotFoundException(String message) {
        super(message);
    }
}
