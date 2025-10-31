package com.group_three.food_ordering.exceptions;

public class InvalidInvitationException extends RuntimeException {

    public InvalidInvitationException(String message, Throwable cause) {
        super(message, cause);
    }
     public InvalidInvitationException(String message) {
        super(message);
     }
}
