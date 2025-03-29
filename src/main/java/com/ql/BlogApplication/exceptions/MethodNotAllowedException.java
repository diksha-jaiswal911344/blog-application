package com.ql.BlogApplication.exceptions;

// handles 405 when client uses the worng HTTP method(e.g., POST instead of GET)
public class MethodNotAllowedException extends RuntimeException{
    public MethodNotAllowedException(String message){
        super(message);
    }
}
