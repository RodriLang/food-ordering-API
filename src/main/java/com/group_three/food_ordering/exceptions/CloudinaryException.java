package com.group_three.food_ordering.exceptions;

public class CloudinaryException extends RuntimeException {
    public CloudinaryException(String message, Throwable cause) {
        super(message, cause);
    }
}
