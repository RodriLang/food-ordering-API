package com.group_three.food_ordering.exceptions;

public class MissingTenantContextException extends RuntimeException {

    private static final String MESSAGE = "Tenant context could not be determined";

    public MissingTenantContextException() {
        super(MESSAGE);
    }

    public MissingTenantContextException(String message) {
        super(message);
    }

    public MissingTenantContextException(String message, Throwable cause) {
        super(message, cause);
    }
}

