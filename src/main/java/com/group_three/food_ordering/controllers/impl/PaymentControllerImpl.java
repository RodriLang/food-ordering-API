package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.controllers.PaymentController;
import com.group_three.food_ordering.dto.request.PaymentRequestDto;
import com.group_three.food_ordering.dto.response.PaymentResponseDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.services.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.PAYMENT_URI)
@RequiredArgsConstructor
public class PaymentControllerImpl implements PaymentController {

    private final PaymentService paymentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'CLIENT', 'GUEST', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<PaymentResponseDto> createPayment(
            PaymentRequestDto dto) {
        PaymentResponseDto createdPayment = paymentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<PaymentResponseDto> payments = paymentService.getAll();
        return ResponseEntity.ok(payments);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            UUID id) {
        PaymentResponseDto response = paymentService.getById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<PaymentResponseDto> updatePayment(
            UUID id,
            PaymentRequestDto dto) {
        PaymentResponseDto response = paymentService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<PaymentResponseDto> cancelPayment(
            UUID id) {
        PaymentResponseDto response = paymentService.updateStatus(id, PaymentStatus.CANCELLED);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<PaymentResponseDto> completePayment(
            UUID id) {
        PaymentResponseDto response = paymentService.updateStatus(id, PaymentStatus.COMPLETED);
        return ResponseEntity.ok(response);
    }
}
