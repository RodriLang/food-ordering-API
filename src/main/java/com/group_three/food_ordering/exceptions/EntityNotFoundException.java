package com.group_three.food_ordering.exceptions;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private final String entityName;

    public EntityNotFoundException(String entityName, String id) {
        super(entityName + " not found with ID: " + id);
        this.entityName = entityName;
    }

    public EntityNotFoundException(String entityName) {
        super(entityName + " not found.");
        this.entityName = entityName;
    }

    public EntityNotFoundException(String entityName, String id, Throwable cause) {
        super(entityName + " not found with ID: " + id, cause);
        this.entityName = entityName;
    }

    public EntityNotFoundException(String entityName, Throwable cause) {
        super("Entity not found.", cause);
        this.entityName = entityName;
    }
}
