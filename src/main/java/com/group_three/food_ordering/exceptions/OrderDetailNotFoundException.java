package com.group_three.food_ordering.exceptions;

public class OrderDetailNotFoundException extends RuntimeException {

    public OrderDetailNotFoundException() {
        super("Order detail not found");
    }
}
