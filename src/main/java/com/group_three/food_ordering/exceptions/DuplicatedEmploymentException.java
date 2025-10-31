package com.group_three.food_ordering.exceptions;

public class DuplicatedEmploymentException extends RuntimeException {

    private static final String MESSAGE = "User already has an employment for this role";

    private static final String PARAMETER_MESSAGE = "Duplicated employment for user=%s at food venue=%s with role=%s ";

    public DuplicatedEmploymentException() {
        super(MESSAGE);
    }

    public DuplicatedEmploymentException(String userEmail, String foodVenueId, String role) {
        super(String.format(PARAMETER_MESSAGE,userEmail , foodVenueId, role));
    }

    public DuplicatedEmploymentException(Throwable cause) {
        super(cause);
    }

    public DuplicatedEmploymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
