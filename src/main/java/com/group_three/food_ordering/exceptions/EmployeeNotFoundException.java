package com.group_three.food_ordering.exceptions;

import java.util.UUID;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(UUID id) {
        super("Employee not found with ID: " + id);
    }
}