package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.PaymentRequestDto;
import com.group_three.food_ordering.dto.response.PaymentResponseDto;
import com.group_three.food_ordering.dto.update.PaymentUpdateDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InvalidPaymentStatusException;
import com.group_three.food_ordering.mappers.PaymentMapper;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.Payment;
import com.group_three.food_ordering.repositories.OrderRepository;
import com.group_three.food_ordering.repositories.PaymentRepository;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final PaymentMapper paymentMapper;

    // Revisar muy muy MUY bien lo que hace este metodo porque es muy importante
    @Transactional
    @Override
    public PaymentResponseDto create(PaymentRequestDto dto) {
        List<Order> orders = findOrders(dto.getOrderIds());

        if (orders.size() != dto.getOrderIds().size()) {
            throw new IllegalArgumentException("Algunas órdenes no fueron encontradas.");
        }

        Payment payment = paymentMapper.toEntity(dto);
        payment.calculateAmount();

        // Asignar el payment a cada orden
        orders.forEach(order -> order.setPayment(payment));

        return paymentMapper.toDTO(paymentRepository.save(payment));
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
        return paymentMapper.toDTO(paymentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment", id.toString())));
    }

    @Transactional
    @Override
    public PaymentResponseDto update(UUID id, PaymentUpdateDto dto) {

        Payment existingPayment = getPaymentEntityByID(id);

        // Verifica si el pago se encuentra en un estado que permita la modificación
        verifyUpdatablePayment(existingPayment);

        // Solo modifica el atributo si el parámetro tiene contenido
        if (dto.getPaymentMethod() != null) {
            existingPayment.setPaymentMethod(dto.getPaymentMethod());
        }

        if (dto.getOrderIds() != null && !dto.getOrderIds().isEmpty()) {
            List<Order> newOrders = findOrders(dto.getOrderIds());

            List<UUID> invalidOrders = new java.util.ArrayList<>();

            for (Order order : newOrders) {
                Payment payment = order.getPayment();

                if (payment != null && (payment.getStatus() == PaymentStatus.PENDING
                        || payment.getStatus() == PaymentStatus.COMPLETED)) {
                    invalidOrders.add(order.getId());
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
        existingPayment.calculateAmount();

        return paymentMapper.toDTO(paymentRepository.save(existingPayment));

    }


    @Override
    public PaymentResponseDto updateStatus(UUID id, PaymentStatus paymentStatus) {
        Payment existingPayment = getPaymentEntityByID(id);

        this.verifyUpdatablePayment(existingPayment);

        if (paymentStatus != null) {
            existingPayment.setStatus(paymentStatus);
        }
        return paymentMapper.toDTO(paymentRepository.save(existingPayment));
    }


    @Override
    public void delete(UUID id) {
        if (!paymentRepository.existsByIdAndDeletedFalse(id)) {
            throw new EntityNotFoundException("Payment", id.toString());
        }
        paymentRepository.deleteById(id);
    }

    private void verifyUpdatablePayment(Payment payment) {
        if (payment.getStatus() == PaymentStatus.COMPLETED
                || payment.getStatus() == PaymentStatus.CANCELLED) {
            throw new InvalidPaymentStatusException(payment.getId(), payment.getStatus(),
                    "El pago no puede ser modificado");
        }
    }

    private Payment getPaymentEntityByID(UUID id) {
        return paymentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment", id.toString()));
    }

    private List<Order> findOrders(List<UUID> orderIds) {
        return orderIds.stream()
                .map(orderService::getEntityByIdAndTenantContext)
                .toList();
    }
}