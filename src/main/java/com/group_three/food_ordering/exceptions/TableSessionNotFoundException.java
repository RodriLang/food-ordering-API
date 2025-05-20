package com.group_three.food_ordering.exceptions;

public class TableSessionNotFoundException extends RuntimeException {

    public TableSessionNotFoundException(String message) {
        super(message);
    }

    public TableSessionNotFoundException() {
        super("Table not found");
    }

    public TableSessionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TableSessionNotFoundException(Long id) {
        super("Table not found with ID: " + id);
    }

    public TableSessionNotFoundException(Long id, Throwable cause) {
        super("Table not found with ID: " + id, cause);
    }
}

