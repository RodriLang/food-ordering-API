package com.group_three.food_ordering.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityName, String id) {
        super(entityName + " not found with id " + id);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(String entityName, String id, Throwable cause) {
        super(entityName + " not found with ID: " + id, cause);
    }
}
