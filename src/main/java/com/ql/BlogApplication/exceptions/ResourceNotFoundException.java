package com.ql.BlogApplication.exceptions;

//used when resources like user, product are not found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message){
        super(message);
    }
}
