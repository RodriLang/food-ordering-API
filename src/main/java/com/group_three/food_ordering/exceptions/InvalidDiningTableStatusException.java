package com.group_three.food_ordering.exceptions;

import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.enums.PaymentStatus;

import java.util.UUID;

public class InvalidDiningTableStatusException extends RuntimeException {

    private static final String MESSAGE = "Invalid table status: ";

    public InvalidDiningTableStatusException(UUID diningTableId, DiningTableStatus status) {
        super(MESSAGE + diningTableId + ", " + status);
    }

    public InvalidDiningTableStatusException(String message) {
        super(message);
    }

    public InvalidDiningTableStatusException(UUID diningTableId, DiningTableStatus status, Throwable cause) {
        super(MESSAGE + diningTableId + ", " + status, cause);
    }

    public InvalidDiningTableStatusException(UUID diningTableId, DiningTableStatus status, String message) {
        super(MESSAGE + diningTableId + ", " + status + ", " + message);
    }
}

