package com.group_three.food_ordering.exceptions;

import lombok.Getter;

@Getter
public class DuplicatedEmploymentException extends RuntimeException {

    private static final String MESSAGE = "User already has an employment for this role";

    private static final String PARAMETER_MESSAGE = "Duplicated employment for user=%s at food venue=%s with role=%s ";

    private final String appCode;

    private static final String APP_CODE = "DUPLICATED_EMPLOYMENT";

    public DuplicatedEmploymentException() {
        super(MESSAGE);
        this.appCode = APP_CODE;
    }

    public DuplicatedEmploymentException(String userEmail, String foodVenueId, String role) {
        super(String.format(PARAMETER_MESSAGE,userEmail , foodVenueId, role));
        this.appCode = APP_CODE;

    }

    public DuplicatedEmploymentException(Throwable cause) {
        super(cause);
        this.appCode = APP_CODE;

    }

    public DuplicatedEmploymentException(String message, Throwable cause) {
        super(message, cause);
        this.appCode = APP_CODE;

    }
}
