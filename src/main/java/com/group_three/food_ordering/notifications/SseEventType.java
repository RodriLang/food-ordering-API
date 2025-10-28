package com.group_three.food_ordering.notifications;

import lombok.Getter;

@Getter
public enum SseEventType {

    // --- Notification Events ---
    NEW_ORDER("new-order"),
    ORDER_CONFIRMED("order-confirmed"),
    ORDER_SERVED("order-served"),
    ORDER_CANCELLED("order-cancelled"),
    SPECIAL_OFFER("special-offer"),
    NEW_MESSAGE("new-message"),

    // --- Table Session Events ---
    USER_JOINED("user-joined"),
    USER_LEFT("user-left"),
    COUNT_UPDATED("count-updated"),

    // --- Connection Event ---
    CONNECTION_SUCCESSFUL("connection-successful");

    private final String eventName;

    SseEventType(String eventName) {
        this.eventName = eventName;
    }
}
