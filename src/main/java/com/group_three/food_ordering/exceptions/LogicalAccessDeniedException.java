package com.group_three.food_ordering.exceptions;

public class LogicalAccessDeniedException extends RuntimeException {
    public LogicalAccessDeniedException(String message) {
        super(message);
    }
}
