package com.example.userservice.exception;

public class DuplicateNationalIdException extends RuntimeException {
    public DuplicateNationalIdException(String message) {
        super(message);
    }
}