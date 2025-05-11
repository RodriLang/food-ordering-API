package com.group_three.food_ordering.exceptions;

import java.util.UUID;

public class FoodVenueNotFoundException extends RuntimeException {

    public FoodVenueNotFoundException(String message) {
        super(message);
    }

    public FoodVenueNotFoundException() {
        super("Food Venue not found");
    }

    public FoodVenueNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public FoodVenueNotFoundException(UUID id) {
        super("Food Venue not found with ID: " + id);
    }

    public FoodVenueNotFoundException(UUID id, Throwable cause) {
        super("Food Venue not found with ID: " + id, cause);
    }
}
