package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.PaymentRequestDto;
import com.group_three.food_ordering.dto.response.PaymentResponseDto;
import com.group_three.food_ordering.utils.constants.ApiDocConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping(ApiPaths.PAYMENT_URI)
@Tag(name = "Pagos", description = "Operaciones para gestionar pagos")
public interface PaymentController {

    @Operation(summary = "Crear un nuevo pago")
    @PostMapping
    ResponseEntity<PaymentResponseDto> createPayment(
            @RequestBody @Valid PaymentRequestDto dto);


    @Operation(summary = "Obtener todos los pagos")
    @GetMapping
    ResponseEntity<List<PaymentResponseDto>> getAllPayments();


    @Operation(summary = "Obtener pago por ID")
    @GetMapping("/{id}")
    ResponseEntity<PaymentResponseDto> getPaymentById(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable UUID id);


    @Operation(summary = "Actualizar un pago")
    @PutMapping("/{id}")
    ResponseEntity<PaymentResponseDto> updatePayment(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable UUID id,
            @RequestBody @Valid PaymentRequestDto dto);


    @Operation(summary = "Cancelar pago",
            description = "Actualizar el estado de un pago a CANCELED. " + ApiDocConstants.PAYMENT_STATE_IRREVERSIBLE
    )
    @PatchMapping("/{id}/cancel")
    ResponseEntity<PaymentResponseDto> cancelPayment(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable UUID id);


    @Operation(summary = "Completar un pago",
            description = "Actualizar el estado de un pago a COMPLETED. " + ApiDocConstants.PAYMENT_STATE_IRREVERSIBLE
    )
    @PatchMapping("/{id}/complete")
    ResponseEntity<PaymentResponseDto> completePayment(
            @Parameter(description = "ID del pago", required = true)
            @PathVariable UUID id);

}
