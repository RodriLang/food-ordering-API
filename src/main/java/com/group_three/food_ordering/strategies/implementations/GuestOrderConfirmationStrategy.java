package com.group_three.food_ordering.strategies.implementations;

import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.Payment;
import com.group_three.food_ordering.strategies.interfaces.OrderConfirmationStrategy;
import org.springframework.stereotype.Component;

@Component
public class GuestOrderConfirmationStrategy implements OrderConfirmationStrategy {
    @Override
    public boolean canConfirm(Order order) {
        Payment payment = order.getPayment();
        return payment != null && payment.getStatus() == PaymentStatus.COMPLETED;
    }
}
