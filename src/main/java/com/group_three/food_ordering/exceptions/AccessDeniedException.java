package com.group_three.food_ordering.exceptions;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String email) {
        super("The email '" + email + "' is already in use.");
    }
}
