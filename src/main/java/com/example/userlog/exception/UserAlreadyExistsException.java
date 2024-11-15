// File: src/main/java/com/auth/exception/UserAlreadyExistsException.java
package com.example.userlog.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}