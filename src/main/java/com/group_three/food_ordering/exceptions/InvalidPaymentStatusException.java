package com.group_three.food_ordering.exceptions;

import com.group_three.food_ordering.enums.PaymentStatus;

import java.util.UUID;

public class InvalidPaymentStatusException extends RuntimeException {

    private static final String MESSAGE = "Invalid payment status: ";

    public InvalidPaymentStatusException(UUID paymentId) {
        super(MESSAGE + paymentId);
    }

    public InvalidPaymentStatusException(UUID paymentId, PaymentStatus status) {
        super(MESSAGE + paymentId + ", " + status);
    }

    public InvalidPaymentStatusException(UUID paymentId, PaymentStatus status, Throwable cause) {
        super(MESSAGE + paymentId + ", " + status, cause);
    }

    public InvalidPaymentStatusException(UUID paymentId, PaymentStatus status, String message) {
        super(MESSAGE + paymentId + ", " + status + ", " + message);
    }
}

