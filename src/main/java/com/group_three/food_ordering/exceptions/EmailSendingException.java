package com.group_three.food_ordering.exceptions;

public class EmailSendingException extends RuntimeException {

    public EmailSendingException(String toEmail, String emailType, Throwable cause) {
        super("Error sending " + emailType + " email to " + toEmail, cause);
    }

    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
