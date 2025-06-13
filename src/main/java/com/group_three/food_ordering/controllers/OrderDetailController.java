package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.OrderDetailRequestDto;
import com.group_three.food_ordering.dtos.response.OrderDetailResponseDto;
import com.group_three.food_ordering.services.interfaces.IOrderDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar los detalles de una orden dentro del sistema de pedidos.
 * <p>
 * Permite crear, consultar y eliminar (borrado lógico) detalles asociados a una orden específica.
 * Cada detalle corresponde a un producto o servicio incluido en una orden.
 * </p>
 */
@RestController
@RequestMapping(ApiPaths.ORDER_BASE)
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService orderDetailService;

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CLIENT','INVITED', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Agregar detalle a una orden", description = "Agrega un nuevo detalle (producto, cantidad, instrucciones) a una orden existente.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle creado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDetailResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida (datos erróneos o faltantes)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @PostMapping(ApiPaths.ORDER_DETAIL_URI)
    public ResponseEntity<OrderDetailResponseDto> addOrderDetailToOrder(
            @Parameter(description = "UUID de la orden a la cual se agrega el detalle", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "Detalle de la orden a crear", required = true)
            @Valid @RequestBody OrderDetailRequestDto orderDetailRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                orderDetailService.create(orderId, orderDetailRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Obtener detalles de una orden", description = "Obtiene la lista de todos los detalles asociados a una orden específica.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles obtenida correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = OrderDetailResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada", content = @Content)
    })
    @GetMapping(ApiPaths.ORDER_DETAIL_URI)
    public ResponseEntity<List<OrderDetailResponseDto>> getOrderDetailsByOrderId(
            @Parameter(description = "UUID de la orden a consultar", required = true)
            @PathVariable UUID orderId) {
        return ResponseEntity.ok(orderDetailService.getOrderDetailsByOrderId(orderId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CLIENT','INVITED', 'SUPER_ADMIN','ROOT')")
    @Operation(summary = "Eliminar un detalle de una orden (borrado lógico)", description = "Realiza un borrado lógico de un detalle específico de una orden, marcándolo como inactivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle eliminado correctamente (soft delete)", content = @Content),
            @ApiResponse(responseCode = "404", description = "Orden o detalle no encontrados", content = @Content)
    })
    @DeleteMapping("/{orderId}/details/{orderDetailId}")
    public ResponseEntity<Void> removeOrderDetailFromOrder(
            @Parameter(description = "UUID de la orden a la que pertenece el detalle", required = true)
            @PathVariable UUID orderId,
            @Parameter(description = "ID del detalle de orden a eliminar", required = true)
            @PathVariable Long orderDetailId) {
        orderDetailService.softDelete(orderId, orderDetailId);
        return ResponseEntity.noContent().build();
    }
}
