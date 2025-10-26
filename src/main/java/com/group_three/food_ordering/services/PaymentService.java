package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.PaymentRequestDto;
import com.group_three.food_ordering.dto.response.PaymentResponseDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDto create(PaymentRequestDto dto);

    Page<PaymentResponseDto> getAllByContextAndStatusAndDateBetween(PaymentStatus status, Instant from, Instant to, Pageable pageable);

    Page<PaymentResponseDto> getAllByTableSessionAndStatus(UUID tableSession, PaymentStatus status, Pageable pageable);

    Page<PaymentResponseDto> getAllByCurrentTableSessionAndStatus(PaymentStatus status, Pageable pageable);

    Page<PaymentResponseDto> getAllOwnPaymentsAndStatus(PaymentStatus status, Pageable pageable);

    Page<PaymentResponseDto> findByOrdersAndStatus(List<UUID> orderIds, PaymentStatus status, Pageable pageable);

    Page<PaymentResponseDto> findAllPaymentsForToday(PaymentStatus status, Pageable pageable);

    PaymentResponseDto getById(UUID id);

    PaymentResponseDto update(UUID id, PaymentRequestDto dto);

    PaymentResponseDto updateStatus(UUID id, PaymentStatus paymentStatus);

    void delete(UUID id);
}
