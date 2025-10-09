package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.PaymentRequestDto;
import com.group_three.food_ordering.dto.response.PaymentResponseDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InvalidPaymentStatusException;
import com.group_three.food_ordering.mappers.PaymentMapper;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.Payment;
import com.group_three.food_ordering.repositories.PaymentRepository;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PaymentMapper paymentMapper;

    private static final String ENTITY_NAME = "Payment";

    // Revisar muy muy MUY bien lo que se hace aca porque es muy importante
    @Transactional
    @Override
    public PaymentResponseDto create(PaymentRequestDto dto) {
        List<UUID> orderIds = dto.getOrderIds();
        orderIds.forEach(order -> log.debug("[PaymentService] Generating payment for order={}", order));
        List<Order> orders = findAndVerifyOrders(orderIds);

        if (orders.size() != dto.getOrderIds().size()) {
            throw new IllegalArgumentException("Algunas órdenes no fueron encontradas.");
        }


        Payment payment = paymentMapper.toEntity(dto);
        payment.setOrders(orders);
        payment.setAmount(calculateAmount(orders));
        payment.setPublicId(UUID.randomUUID());
        payment.setStatus(PaymentStatus.PENDING);
        // Asignar el payment a cada orden
        log.debug("[PaymentService] Assigning payment to orders");
        orders.forEach(order -> {
            log.debug("[PaymentService] Assigning payment={} to order={}", payment, order);
            order.setPayment(payment);
        });
        Payment paymentSaved = paymentRepository.save(payment);
        return paymentMapper.toDTO(paymentSaved);
    }

    @Override
    public List<PaymentResponseDto> getAll() {
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::toDTO)
                .toList();
    }

    @Override
    public PaymentResponseDto getById(UUID id) {
        Payment payment = getPaymentEntityById(id);
        return paymentMapper.toDTO(payment);
    }

    @Transactional
    @Override
    public PaymentResponseDto update(UUID id, PaymentRequestDto dto) {
        log.debug("[PaymentService] Updating order");
        Payment existingPayment = getPaymentEntityById(id);

        // Verifica si el pago se encuentra en un estado que permita la modificación
        verifyUpdatablePayment(existingPayment);

        // Solo modifica el atributo si el parámetro tiene contenido
        if (dto.getPaymentMethod() != null) {
            existingPayment.setPaymentMethod(dto.getPaymentMethod());
        }

        if (dto.getOrderIds() != null && !dto.getOrderIds().isEmpty()) {
            List<Order> newOrders = findAndVerifyOrders(dto.getOrderIds());

            List<UUID> invalidOrders = new java.util.ArrayList<>();

            for (Order order : newOrders) {
                Payment payment = order.getPayment();

                if (payment != null && (payment.getStatus() == PaymentStatus.PENDING
                        || payment.getStatus() == PaymentStatus.COMPLETED)) {
                    invalidOrders.add(order.getPublicId());
                } else if ((payment == null || payment.getStatus() == PaymentStatus.CANCELLED)
                        && !existingPayment.getOrders().contains(order)) {

                    order.setPayment(existingPayment);
                    existingPayment.getOrders().add(order);
                }
            }

            if (!invalidOrders.isEmpty()) {
                throw new IllegalArgumentException(
                        "Las siguientes órdenes ya tienen un pago válido asociado: " + invalidOrders);
            }
        }
        existingPayment.setAmount(calculateAmount(existingPayment.getOrders()));

        return paymentMapper.toDTO(paymentRepository.save(existingPayment));
    }

    @Override
    public PaymentResponseDto updateStatus(UUID id, PaymentStatus paymentStatus) {
        Payment existingPayment = getPaymentEntityById(id);

        this.verifyUpdatablePayment(existingPayment);

        if (paymentStatus != null) {
            existingPayment.setStatus(paymentStatus);
        }
        return paymentMapper.toDTO(paymentRepository.save(existingPayment));
    }

    @Override
    public void delete(UUID id) {
        if (!paymentRepository.existsByPublicId(id)) {
            throw new EntityNotFoundException(ENTITY_NAME, id.toString());
        }
        paymentRepository.deleteByPublicId(id);
    }

    private void verifyUpdatablePayment(Payment payment) {
        if (payment.getStatus() == PaymentStatus.COMPLETED
                || payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new InvalidPaymentStatusException(payment.getPublicId(), payment.getStatus(),
                    "El pago no puede ser modificado");
        }
    }

    private Payment getPaymentEntityById(UUID id) {
        return paymentRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id.toString()));
    }

    private List<Order> findAndVerifyOrders(List<UUID> orderIds) {
        List<Order> orders = orderIds.stream()
                .map(orderService::getEntityById)
                .filter(order -> order.getPayment() == null ||
                        order.getPayment().getStatus().equals(PaymentStatus.CANCELLED)).toList();

        orders.forEach(order -> log.debug("[PaymentService] Calculating amount for order={}", order.getPublicId()));
        return orders;
    }

    private BigDecimal calculateAmount(List<Order> orders) {
        BigDecimal amount;
        if (orders != null) {
            log.debug("[PaymentService] Calculating amount for orders");
            amount = orders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            log.debug("[PaymentService] There are not orders");
            amount = BigDecimal.ZERO;
        }
        log.debug("[PaymentService] Amount calculated={}", amount);
        return amount;
    }
}