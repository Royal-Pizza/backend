package com.example.royalpizza.exception;

public abstract class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}
