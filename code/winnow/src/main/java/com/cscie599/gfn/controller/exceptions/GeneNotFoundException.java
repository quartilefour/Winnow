package com.cscie599.gfn.controller.exceptions;

public class GeneNotFoundException extends RuntimeException {
    public GeneNotFoundException(String message) {
        super(message);
    }
}
