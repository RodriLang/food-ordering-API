package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.OrderRequestDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RequestMapping(ApiPaths.ORDER_BASE)
public interface OrderController {

    @PreAuthorize("hasAnyRole('CLIENT','INVITED')")
    @Operation(
            summary = "Crear una nueva orden",
            description = "Crea una orden con los datos proporcionados en el cuerpo de la solicitud."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    @PostMapping
    ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto order);


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener órdenes con filtros opcionales",
            description = "Devuelve una lista de órdenes que pueden ser filtradas por rango de fechas y estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Órdenes recuperadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping()
    ResponseEntity<List<OrderResponseDto>> getOrders(
            @Parameter(description = "Fecha desde la cual buscar órdenes (formato yyyy-MM-dd)", example = "2025-05-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @Parameter(description = "Fecha hasta la cual buscar órdenes (formato yyyy-MM-dd)", example = "2025-05-10")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

            @Parameter(description = "Estado de la orden para filtrar", example = "PENDING")
            @RequestParam(required = false) OrderStatus status);


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener las órdenes del dia",
            description = "Devuelve una lista de órdenes del dia en curso que pueden ser filtralas por estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Órdenes recuperadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping("/today")
    ResponseEntity<List<OrderResponseDto>> getDailyOrders(
            @Parameter(description = "Estado de la orden para filtrar", example = "PENDING")
            @RequestParam(required = false) OrderStatus status);


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener una orden por ID",
            description = "Devuelve los detalles de una orden específica identificada por su UUID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    ResponseEntity<OrderResponseDto> getOrderById(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id);


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener una orden por fecha y número de orden",
            description = "Devuelve los detalles de una orden específica identificada por la fecha y el número de orden del día."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping("/by-date/{date}/number/{orderNumber}")
    ResponseEntity<OrderResponseDto> getOrderByDateAndOrderNumber(
            @Parameter(description = "Fecha de la orden en formato yyyy-MM-dd", example = "2025-05-25")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "Número de orden del día", example = "15")
            @PathVariable Integer orderNumber);


    @PreAuthorize("hasAnyRole('CLIENT', 'INVITED')")
    @Operation(
            summary = "Actualizar requisitos especiales de una orden",
            description = "Actualiza los requisitos especiales o notas específicas asociados a una orden."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Requisitos actualizados exitosamente"),
            @ApiResponse(responseCode = "400", description = "Requisitos inválidos"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/requirements")
    ResponseEntity<OrderResponseDto> updateOrderRequirements(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,

            @Parameter(description = "Requisitos especiales o notas", example = "Sin cebolla, extra picante")
            @RequestParam @Size(max = 255) String requirements);


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CLIENT','INVITED', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Cancelar una orden",
            description = "Marca una orden como cancelada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/cancel")
    ResponseEntity<OrderResponseDto> cancelOrder(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id);


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Actualizar el estado de una orden",
            description = "Actualiza el estado de una orden. Ejemplo: PENDING, SERVED, CANCELLED."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/status")
    ResponseEntity<OrderResponseDto> updateOrderStatus(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,

            @Parameter(description = "Nuevo estado de la orden", example = "COMPLETED")
            @RequestParam OrderStatus status);
}