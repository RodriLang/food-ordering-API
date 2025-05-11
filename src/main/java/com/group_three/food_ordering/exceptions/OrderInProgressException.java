package com.group_three.food_ordering.exceptions;

import java.util.UUID;

public class OrderInProgressException extends RuntimeException {

    public OrderInProgressException(String message) {
        super(message);
    }

    public OrderInProgressException() {
        super("Order already in progress");
    }

    public OrderInProgressException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderInProgressException(UUID id) {
        super("Order ID " + id + " already in progress");
    }

    public OrderInProgressException(UUID id, Throwable cause) {
        super("Order ID " + id + " already in progress", cause);
    }
}
