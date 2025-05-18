package com.group_three.food_ordering.services.interfaces;


import com.group_three.food_ordering.dtos.create.PaymentRequestDto;
import com.group_three.food_ordering.dtos.response.PaymentResponseDto;
import com.group_three.food_ordering.enums.PaymentStatus;

import java.util.List;
import java.util.UUID;

public interface IPaymentService {

    PaymentResponseDto create(PaymentRequestDto paymentRequestDto);
    List<PaymentResponseDto> getAll();
    PaymentResponseDto getById(UUID id);
    void delete(UUID id);
    PaymentResponseDto updateStatus(UUID paymentId, PaymentStatus paymentStatus);
}
