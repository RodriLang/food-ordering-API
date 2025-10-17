package com.group_three.food_ordering.exceptions;

public class InvalidJsonException extends RuntimeException {
    public InvalidJsonException(String message, Throwable cause) {
        super(message, cause);
    }
}
