package com.group_three.food_ordering.exceptions;

public class TableNotFoundException extends RuntimeException {

    public TableNotFoundException(String message) {
        super(message);
    }

    public TableNotFoundException() {
        super("Table not found");
    }

    public TableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableNotFoundException(Long id) {
        super("Table not found with ID: " + id);
    }

    public TableNotFoundException(Long id, Throwable cause) {
        super("Table not found with ID: " + id, cause);
    }
}

