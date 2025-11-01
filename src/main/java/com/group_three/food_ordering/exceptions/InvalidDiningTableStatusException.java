package com.group_three.food_ordering.exceptions;

import com.group_three.food_ordering.enums.DiningTableStatus;
import lombok.Getter;

import java.util.UUID;

@Getter
public class InvalidDiningTableStatusException extends RuntimeException {

    private static final String MESSAGE = "Invalid table status: ";
    private final DiningTableStatus diningTableStatus;

    public InvalidDiningTableStatusException(UUID diningTableId, DiningTableStatus status) {
        super(MESSAGE + diningTableId + ", " + status);
        diningTableStatus = status;
    }
}

