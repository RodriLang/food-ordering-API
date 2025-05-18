package com.group_three.food_ordering.exceptions;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException() {
        super("Insufficient Stock");
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientStockException(String productName, Integer stock) {
        super("Product Name: "+productName+". Disponible stock: "+stock);
    }
}
