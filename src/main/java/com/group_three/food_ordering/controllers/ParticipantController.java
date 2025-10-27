package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.response.*;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping(ApiPaths.PARTICIPANT_URI)
@Tag(name = "Participantes", description = "Gestión de clientes registrados e invitados asociados a una sesión de mesa.")
public interface ParticipantController {

    @Operation(
            summary = "Obtener todas las órdenes",
            description = "Devuelve la lista con los pedidos realizados por participante autenticado o invitado.",
            responses = {
                    @ApiResponse(responseCode = "200")
            }
    )
    @GetMapping("/orders")
    ResponseEntity<PageResponse<OrderResponseDto>> getCurrentOrders(@Parameter Pageable pageable);


    @Operation(
            summary = "Delegar funciones de anfitrión",
            description = "El host actual de la mesa puede designar a otro participante para que cumpla esa función.",
            responses = {
                    @ApiResponse(responseCode = "200"),
                    @ApiResponse(responseCode = "404")
            }
    )
    @PatchMapping("/host/{participantId}")
    ResponseEntity<AuthResponse> delegateHostingDuties(@PathVariable UUID participantId);


    @Operation(summary = "Obtener pagos por sesión de mesa actual y estado")
    @GetMapping("/table-sessions/payments")
    ResponseEntity<PageResponse<PaymentResponseDto>> getAllPaymentsByCurrentTableSessionAndStatus(
            @RequestParam PaymentStatus status,
            @Parameter Pageable pageable);



    @Operation(summary = "Obtener pagos por sesión de mesa actual y estado")
    @GetMapping("/payments")
    ResponseEntity<PageResponse<PaymentResponseDto>> getAllOwnPayments(
            @RequestParam PaymentStatus status,
            @Parameter Pageable pageable);

    @Operation(summary = "Obtener ordenes por sesión de mesa actual y estado")
    @GetMapping("/table-sessions/orders")
    ResponseEntity<PageResponse<OrderResponseDto>> getAllOrdersByCurrentTableSessionAndStatus(
            @RequestParam OrderStatus status,
            @Parameter Pageable pageable);


    @Operation(
            summary = "Obtener la sesión actual",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones pasadas por participante",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table-sessions")
    ResponseEntity<TableSessionResponseDto> getCurrentTableSession();


    @Operation(
            summary = "Finalizar la sesión de mesa propia (Host)",
            description = "Actualiza el endTime de la sesión y coloca la mesa en estado WAITING_RESET hasta que sea limpiada y colocada en AVAILABLE por un STAFF.)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sesión creada exitosamente",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "404", description = "Session de mesa no encontrada")
            }
    )
    @PatchMapping("/end")
    ResponseEntity<Void> endYourOwnTableSession();


}
