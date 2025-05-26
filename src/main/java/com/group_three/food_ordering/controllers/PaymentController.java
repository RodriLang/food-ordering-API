package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.PaymentRequestDto;
import com.group_three.food_ordering.dtos.response.PaymentResponseDto;
import com.group_three.food_ordering.dtos.update.PaymentUpdateDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.services.interfaces.IPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.PAYMENT_BASE)
@RequiredArgsConstructor
@Tag(name = "payment-controller", description = "Operaciones para gestionar pagos")
public class PaymentController {

    private final IPaymentService paymentService;

    @Operation(summary = "Crear un nuevo pago")
    @PostMapping
    public ResponseEntity<PaymentResponseDto> createPayment(
            @RequestBody @Valid PaymentRequestDto dto) {
        PaymentResponseDto response = paymentService.create(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Obtener todos los pagos")
    @GetMapping
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        List<PaymentResponseDto> payments = paymentService.getAll();
        return ResponseEntity.ok(payments);
    }

    @Operation(summary = "Obtener pago por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> getPaymentById(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable UUID id) {
        PaymentResponseDto response = paymentService.getById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar un pago")
    @PutMapping("/{id}")
    public ResponseEntity<PaymentResponseDto> updatePayment(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable UUID id,
            @RequestBody @Valid PaymentUpdateDto dto) {
        PaymentResponseDto response = paymentService.update(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar el estado de un pago")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponseDto> updatePaymentStatus(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable UUID id,
            @RequestParam PaymentStatus status) {
        PaymentResponseDto response = paymentService.updateStatus(id, status);
        return ResponseEntity.ok(response);
    }
}
