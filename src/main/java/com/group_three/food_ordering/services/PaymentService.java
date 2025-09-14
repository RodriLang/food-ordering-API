package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.PaymentRequestDto;
import com.group_three.food_ordering.dto.response.PaymentResponseDto;
import com.group_three.food_ordering.dto.update.PaymentUpdateDto;
import com.group_three.food_ordering.enums.PaymentStatus;

import java.util.List;
import java.util.UUID;

public interface PaymentService {
    PaymentResponseDto create(PaymentRequestDto dto);
    List<PaymentResponseDto> getAll();
    PaymentResponseDto getById(UUID id);
    PaymentResponseDto update(UUID id, PaymentUpdateDto dto);
    PaymentResponseDto updateStatus(UUID id, PaymentStatus paymentStatus);
    void delete(UUID id);
}
