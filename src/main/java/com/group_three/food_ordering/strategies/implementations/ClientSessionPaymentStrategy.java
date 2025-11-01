package com.group_three.food_ordering.strategies.implementations;

import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.Payment;
import com.group_three.food_ordering.repositories.OrderRepository;
import com.group_three.food_ordering.repositories.PaymentRepository;
import com.group_three.food_ordering.strategies.interfaces.PaymentCommand;
import com.group_three.food_ordering.strategies.interfaces.PaymentResult;
import com.group_three.food_ordering.strategies.interfaces.PaymentScope;
import com.group_three.food_ordering.strategies.interfaces.PaymentStrategy;
import com.group_three.food_ordering.utils.EntityName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GuestPaymentStrategy implements PaymentStrategy {

    private final OrderRepository orderRepo;
    private final PaymentRepository paymentRepo;

    @Transactional
    @Override
    public PaymentResult settle(PaymentCommand cmd) {
        if (cmd.getScope() != PaymentScope.ORDER || cmd.getOrderId() == null)
            throw new IllegalArgumentException("Guest must pay a single ORDER.");

        Order order = orderRepo.findByPublicId(cmd.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException(EntityName.ORDER, cmd.getOrderId().toString()));

        // idempotencia
        if (order.getPayment() != null && order.getPayment().getStatus() == PaymentStatus.COMPLETED) {
            Payment p = order.getPayment();
            return PaymentResult.builder()
                    .status(p.getStatus())
                    .totalCaptured(p.getAmount())
                    .coveredOrders(List.of(order.getPublicId()))
                    .build();
        }

        Payment payment = Payment.builder()
                .amount(order.getTotalPrice())
                .paymentMethod(cmd.getMethod())
                .paymentDate(Instant.now())
                .status(PaymentStatus.COMPLETED)
                .build();

        payment = paymentRepo.save(payment);
        order.setPayment(payment);
        orderRepo.save(order);

        return PaymentResult.builder()
                .status(payment.getStatus())
                .totalCaptured(payment.getAmount())
                .coveredOrders(List.of(order.getPublicId()))
                .build();
    }
}
