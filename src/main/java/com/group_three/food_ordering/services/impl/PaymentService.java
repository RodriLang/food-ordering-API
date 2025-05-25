package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.dtos.create.PaymentRequestDto;
import com.group_three.food_ordering.dtos.response.PaymentResponseDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.exceptions.PaymentNotFoundException;
import com.group_three.food_ordering.mappers.PaymentMapper;
import com.group_three.food_ordering.models.Payment;
import com.group_three.food_ordering.repositories.IPaymentRepository;
import com.group_three.food_ordering.services.interfaces.IPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final IPaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;


    @Override
    public PaymentResponseDto create(PaymentRequestDto paymentRequestDto) {

        Payment payment = paymentMapper.toEntity(paymentRequestDto);
        return paymentMapper.toDTO(paymentRepository.save(payment));
    }

    @Override
    public List<PaymentResponseDto> getAll() {
        return paymentRepository.findAll().stream()
                .map(paymentMapper::toDTO)
                .toList();
    }

    @Override
    public PaymentResponseDto getById(UUID id) {
        return paymentMapper.toDTO(paymentRepository.findById(id)
                .orElseThrow(PaymentNotFoundException::new));
    }

    @Override
    public void delete(UUID id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public PaymentResponseDto updateStatus(UUID paymentId, PaymentStatus paymentStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(PaymentNotFoundException::new);

        payment.setStatus(paymentStatus);

        paymentRepository.save(payment);

        return null;
    }
}
