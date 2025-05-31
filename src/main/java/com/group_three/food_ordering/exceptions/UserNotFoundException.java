package com.group_three.food_ordering.exceptions;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(UUID id) { super("UserEntity not found with ID: " + id);
    }
}
