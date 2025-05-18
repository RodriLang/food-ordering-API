package com.group_three.food_ordering.strategies.interfaces;

import com.group_three.food_ordering.models.Order;

public interface OrderConfirmationStrategy {
    boolean canConfirm(Order order);
}
