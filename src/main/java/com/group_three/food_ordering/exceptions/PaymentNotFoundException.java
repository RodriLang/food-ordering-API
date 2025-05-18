package com.group_three.food_ordering.exceptions;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }

    public PaymentNotFoundException() {
        super("Payment not found");
    }

    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PaymentNotFoundException(Long id) {
        super("Payment not found with ID: " + id);
    }

    public PaymentNotFoundException(Long id, Throwable cause) {
        super("Payment not found with ID: " + id, cause);
    }
}

