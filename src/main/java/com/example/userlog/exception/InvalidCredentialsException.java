// File: src/main/java/com/auth/exception/InvalidCredentialsException.java
package com.example.userlog.exception;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}