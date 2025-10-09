package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.*;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.utils.OnCreate;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RequestMapping(ApiPaths.TABLE_SESSION_URI)
@Tag(name = "Sesiones de mesa", description = "Gestión de las sesiones de mesa, participaciones de clientes")
public interface TableSessionController {

    @Operation(
            summary = "Iniciar una nueva sesión de mesa",
            description = "Crea una sesión asociada a una mesa con los datos proporcionados.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sesión creada exitosamente",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
            }
    )
    @PostMapping("/scan-qr")
    ResponseEntity<InitSessionResponseDto> createTableSession(
            @RequestBody @Validated(OnCreate.class) TableSessionRequestDto tableSessionRequestDto);


    @Operation(
            summary = "Obtener todas las sesiones de mesa",
            description = "Devuelve una lista con todas las sesiones de mesa registradas. Accesible para roles staff y admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping()
    ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByContext(@Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener todas las sesiones de mesa de un Local de comida",
            description = "Devuelve una lista con todas las sesiones de mesa registradas en un lugar específico. Acceso root",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/root")
    ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByFoodVenueId(@Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener sesión por UUID",
            description = "Obtiene una sesión de mesa mediante su identificador UUID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sesión encontrada",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Sesión no encontrada")
            }
    )
    @GetMapping("/{id}")
    ResponseEntity<TableSessionResponseDto> getTableSessionById(
            @Parameter(description = "UUID de la sesión a buscar", required = true)
            @PathVariable UUID id);


    @Operation(
            summary = "Obtener sesiones por número de mesa",
            description = "Devuelve una lista con todas las sesiones asociadas a un número de mesa del Lugar de comida asociado al usuario.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones por mesa",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table/{tableNumber}")
    ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByContextAndTable(
            @Parameter(description = "Número de la mesa", required = true)
            @PathVariable Integer tableNumber,
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener sesiones por número de mesa y rango de tiempo",
            description = "Obtiene sesiones de una mesa dentro de un rango de fechas y horas. El parámetro 'end' es opcional.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones filtradas",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table/{tableNumber}/time-range")
    ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByTableAndTimeRange(
            @Parameter(description = "Número de la mesa", required = true)
            @PathVariable Integer tableNumber,
            @Parameter(description = "Fecha y hora de inicio (ISO 8601)", required = true)
            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Fecha y hora de fin (ISO 8601)")
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener sesiones activas",
            description = "Devuelve una lista con todas las sesiones que están activas actualmente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones activas",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/active")
    ResponseEntity<PageResponse<TableSessionResponseDto>> getActiveSessions(@Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener sesiones por cliente anfitrión",
            description = "Devuelve todas las sesiones donde un cliente específico es el anfitrión.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones por cliente anfitrión",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/host/{clientId}")
    ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByHostClient(
            @Parameter(description = "UUID del cliente anfitrión", required = true)
            @PathVariable UUID clientId,
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener sesiones pasadas por participante",
            description = "Devuelve todas las sesiones pasadas en las que un cliente participó.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones pasadas por participante",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/participant/{clientId}")
    ResponseEntity<PageResponse<TableSessionResponseDto>> getPastTableSessionsByParticipant(
            @Parameter(description = "UUID del cliente participante", required = true)
            @PathVariable UUID clientId,
            @Parameter(hidden = true) Pageable pageable);


    @Operation(
            summary = "Obtener la última sesión de una mesa",
            description = "Devuelve la sesión más reciente asociada a una mesa determinada.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Última sesión encontrada",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "No se encontró sesión para la mesa especificada")
            }
    )
    @GetMapping("/latest/{tableId}")
    ResponseEntity<TableSessionResponseDto> getLatestTableSessionByTable(
            @Parameter(description = "UUID de la mesa", required = true)
            @PathVariable UUID tableId);


    @Operation(
            summary = "Agregar un cliente a una sesión",
            description = "Agrega un cliente participante a una sesión de mesa existente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cliente agregado exitosamente a la sesión",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Sesión o cliente no encontrado")
            }
    )
    @PutMapping("/{id}/clients/{clientId}")
    ResponseEntity<TableSessionResponseDto> addClientToSession(
            @Parameter(description = "UUID de la sesión", required = true)
            @PathVariable UUID id,
            @Parameter(description = "UUID del cliente a agregar", required = true)
            @PathVariable UUID clientId);


    @Operation(
            summary = "Obtener todos los pedidos de la sesión",
            description = "devuelve todas las ordenes asociadas a una sesión de mesa",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class)))
            }
    )
    @GetMapping("/{id}/orders")
    ResponseEntity<PageResponse<OrderResponseDto>> getOrdersByTableSession(
            @Parameter(description = "UUID de la table session", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @RequestParam(required = false) OrderStatus status,
            @Parameter Pageable pageable);


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

    @Operation(
            summary = "Finalizar la sesión de una mesa (Staff)",
            description = "Actualiza el endTime de la sesión y coloca la mesa en estado WAITING_RESET hasta que sea limpiada y colocada en AVAILABLE por un STAFF.)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Sesión creada exitosamente",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
                    @ApiResponse(responseCode = "404", description = "Mesa no encontrada")
            }
    )
    @PatchMapping("/end/{tableSessionId}")
    ResponseEntity<Void> endTableSessionById(@PathVariable UUID tableSessionId);
}
