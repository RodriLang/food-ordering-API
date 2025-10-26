package com.group_three.food_ordering.exceptions;

public class UserSessionConflictException extends RuntimeException {

    public UserSessionConflictException(String message) {
        super(message);
    }

    public UserSessionConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

