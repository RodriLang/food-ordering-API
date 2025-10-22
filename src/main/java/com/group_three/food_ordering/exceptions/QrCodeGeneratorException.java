package com.group_three.food_ordering.exceptions;

public class QrCodeGeneratorException extends RuntimeException {
    public QrCodeGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
