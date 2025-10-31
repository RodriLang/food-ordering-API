package com.group_three.food_ordering.exceptions;

import lombok.Getter;

@Getter
public class InactiveEmploymentStatusException extends RuntimeException {

    private static final String MESSAGE = "There is an inactive employment for that user at that food place with that role.";

    private static final String PARAMETER_MESSAGE = "There is an inactive employment for user=%s at food venue=%s with role=%s ";

    private static final String APP_CODE = "INACTIVE_EMPLOYMENT";

    private final String appCode;

    public InactiveEmploymentStatusException() {
        super(MESSAGE);
        appCode = APP_CODE;
    }

    public InactiveEmploymentStatusException(String userEmail, String foodVenueId, String role) {
        super(String.format(PARAMETER_MESSAGE, userEmail, foodVenueId, role));
        appCode = APP_CODE;
    }

}
