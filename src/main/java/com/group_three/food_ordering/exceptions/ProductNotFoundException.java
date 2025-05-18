package com.group_three.food_ordering.exceptions;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException() {
        super("Product not found");
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }

    public ProductNotFoundException(Long id, Throwable cause) {
        super("Product not found with ID: " + id, cause);
    }
}

