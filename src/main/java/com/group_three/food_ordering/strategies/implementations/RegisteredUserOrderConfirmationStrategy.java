package com.group_three.food_ordering.strategies.implementations;

import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.strategies.interfaces.OrderConfirmationStrategy;
import org.springframework.stereotype.Component;

@Component
public class RegisteredUserOrderConfirmationStrategy implements OrderConfirmationStrategy {
    @Override
    public boolean canConfirm(Order order) {
        return true; // Puede confirmar sin haber pagado aún
    }
}

