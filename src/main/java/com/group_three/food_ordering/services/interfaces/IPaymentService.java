package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.PaymentRequestDto;
import com.group_three.food_ordering.dtos.response.PaymentResponseDto;
import com.group_three.food_ordering.dtos.update.PaymentUpdateDto;
import com.group_three.food_ordering.enums.PaymentStatus;

import java.util.List;
import java.util.UUID;

public interface IPaymentService {
    PaymentResponseDto create(PaymentRequestDto dto);
    List<PaymentResponseDto> getAll();
    PaymentResponseDto getById(UUID id);
    PaymentResponseDto update(UUID id, PaymentUpdateDto dto);
    PaymentResponseDto updateStatus(UUID id, PaymentStatus paymentStatus);
    void delete(UUID id);
}
