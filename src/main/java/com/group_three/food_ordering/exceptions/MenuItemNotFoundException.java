package com.group_three.food_ordering.exceptions;

import java.util.UUID;

public class MenuItemNotFoundException extends RuntimeException {

    public MenuItemNotFoundException(String message) {
        super(message);
    }

    public MenuItemNotFoundException() {
        super("Menu item not found");
    }

    public MenuItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public MenuItemNotFoundException(Long id) {
        super("Menu item not found with ID: " + id);
    }

    public MenuItemNotFoundException(Long id, Throwable cause) {
        super("Menu item not found with ID: " + id, cause);
    }
}
