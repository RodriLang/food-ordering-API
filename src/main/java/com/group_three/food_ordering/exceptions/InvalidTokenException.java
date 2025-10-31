package com.group_three.food_ordering.exceptions;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String invalidOrExpiredRefreshToken) {
        super("Invalid refresh token: " + invalidOrExpiredRefreshToken);
    }

}
