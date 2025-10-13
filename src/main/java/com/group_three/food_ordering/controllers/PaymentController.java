package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.PaymentRequestDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.PaymentResponseDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequestMapping(ApiPaths.PAYMENT_URI)
@Tag(name = "Pagos", description = "Operaciones para gestionar pagos")
public interface PaymentController {

    @Operation(summary = "Crear un nuevo pago")
    @PostMapping
    ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto dto);

    @Operation(summary = "Obtener pagos por contexto, estado y rango de fechas")
    @GetMapping("/context")
    ResponseEntity<PageResponse<PaymentResponseDto>> getAllByContextAndStatusAndDateBetween(
            @RequestParam PaymentStatus status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @Parameter Pageable pageable);

    @Operation(summary = "Obtener pagos por sesión de mesa y estado")
    @GetMapping("/table-session/{tableSession}")
    ResponseEntity<PageResponse<PaymentResponseDto>> getAllByTableSessionAndStatus(
            @PathVariable UUID tableSession,
            @RequestParam PaymentStatus status,
            @Parameter Pageable pageable);

    @Operation(summary = "Obtener pagos por lista de órdenes y estado")
    @GetMapping("/orders")
    ResponseEntity<PageResponse<PaymentResponseDto>> findByOrdersAndStatus(
            @RequestParam List<UUID> orderIds,
            @RequestParam PaymentStatus status,
            @Parameter Pageable pageable);

    @Operation(summary = "Obtener pagos de hoy por estado")
    @GetMapping("/today")
    ResponseEntity<PageResponse<PaymentResponseDto>> findAllPaymentsForToday(
            @RequestParam PaymentStatus status,
            @Parameter Pageable pageable);

    @Operation(summary = "Obtener pago por ID")
    @GetMapping("/{id}")
    ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable UUID id);

    @Operation(summary = "Actualizar un pago")
    @PutMapping("/{id}")
    ResponseEntity<PaymentResponseDto> updatePayment(@PathVariable UUID id, @RequestBody PaymentRequestDto dto);

    @Operation(summary = "Cancelar pago")
    @PatchMapping("/{id}/cancel")
    ResponseEntity<PaymentResponseDto> cancelPayment(@PathVariable UUID id);

    @Operation(summary = "Completar pago")
    @PatchMapping("/{id}/complete")
    ResponseEntity<PaymentResponseDto> completePayment(@PathVariable UUID id);
}
