package com.group_three.food_ordering.exceptions;

import java.util.UUID;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException() {
        super("Order not found");
    }

    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderNotFoundException(UUID id) {
        super("Order not found with ID: " + id);
    }

    public OrderNotFoundException(UUID id, Throwable cause) {
        super("Order not found with ID: " + id, cause);
    }
}
