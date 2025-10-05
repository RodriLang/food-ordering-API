package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.InitSessionResponseDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.TableSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.TABLE_SESSION_URI)
@RequiredArgsConstructor
public class TableSessionController {

    private final TableSessionService tableSessionService;
    private final OrderService orderService;

    @PreAuthorize("hasRole('CLIENT') or isAnonymous()")
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
    public ResponseEntity<InitSessionResponseDto> createTableSession(
            @RequestBody @Valid TableSessionRequestDto tableSessionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(tableSessionService.enter(tableSessionRequestDto));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
    @Operation(
            summary = "Obtener todas las sesiones de mesa",
            description = "Devuelve una lista con todas las sesiones de mesa registradas. Accesible para roles staff y admin",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping()
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByContext() {
        return ResponseEntity.ok(tableSessionService.getAll());
    }


    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Obtener todas las sesiones de mesa de un Local de comida",
            description = "Devuelve una lista con todas las sesiones de mesa registradas en un lugar específico. Acceso root",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/root")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByFoodVenueId() {
        return ResponseEntity.ok(tableSessionService.getAll());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
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
    public ResponseEntity<TableSessionResponseDto> getTableSessionById(
            @Parameter(description = "UUID de la sesión a buscar", required = true)
            @PathVariable UUID id) {
        return ResponseEntity.ok(tableSessionService.getById(id));
    }


    @PreAuthorize("hasRole('ROOT')")
    @Operation(
            summary = "Obtener sesiones por id del local de comida y número de mesa",
            description = "Devuelve una lista con todas las sesiones asociadas a un número de mesa de un Lugar de comida específico.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones por mesa",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("{foodVenueId}/table/{tableNumber}")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByFoodVenueAndTable(
            @Parameter(description = "Número de la mesa", required = true)
            @PathVariable Integer tableNumber,
            @Parameter(description = "Id del FoodVenue", required = true)
            @PathVariable UUID foodVenueId) {
        return ResponseEntity.ok(tableSessionService.getByFoodVenueAndTable(foodVenueId, tableNumber));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
    @Operation(
            summary = "Obtener sesiones por número de mesa",
            description = "Devuelve una lista con todas las sesiones asociadas a un número de mesa del Lugar de comida asociado al usuario.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones por mesa",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByContextAndTable(
            @Parameter(description = "Número de la mesa", required = true)
            @PathVariable Integer tableNumber) {
        return ResponseEntity.ok(tableSessionService.getByContextAndTable(tableNumber));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener sesiones por número de mesa y rango de tiempo",
            description = "Obtiene sesiones de una mesa dentro de un rango de fechas y horas. El parámetro 'end' es opcional.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones filtradas",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/table/{tableNumber}/time-range")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByTableAndTimeRange(
            @Parameter(description = "Número de la mesa", required = true)
            @PathVariable Integer tableNumber,
            @Parameter(description = "Fecha y hora de inicio (ISO 8601)", required = true)
            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @Parameter(description = "Fecha y hora de fin (ISO 8601)")
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(tableSessionService.getByTableAndTimeRange(tableNumber, start, end));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener sesiones activas",
            description = "Devuelve una lista con todas las sesiones que están activas actualmente.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones activas",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/active")
    public ResponseEntity<List<TableSessionResponseDto>> getActiveSessions() {
        return ResponseEntity.ok(tableSessionService.getActiveSessions());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener sesiones por cliente anfitrión",
            description = "Devuelve todas las sesiones donde un cliente específico es el anfitrión.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones por cliente anfitrión",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/host/{clientId}")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByHostClient(
            @Parameter(description = "UUID del cliente anfitrión", required = true)
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(tableSessionService.getByHostClient(clientId));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Operation(
            summary = "Obtener sesiones pasadas por participante",
            description = "Devuelve todas las sesiones pasadas en las que un cliente participó.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lista de sesiones pasadas por participante",
                            content = @Content(schema = @Schema(implementation = TableSessionResponseDto.class, type = "array")))
            }
    )
    @GetMapping("/participant/{clientId}")
    public ResponseEntity<List<TableSessionResponseDto>> getPastTableSessionsByParticipant(
            @Parameter(description = "UUID del cliente participante", required = true)
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(tableSessionService.getPastByParticipant(clientId));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
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
    public ResponseEntity<TableSessionResponseDto> getLatestTableSessionByTable(
            @Parameter(description = "UUID de la mesa", required = true)
            @PathVariable UUID tableId) {
        return ResponseEntity.ok(tableSessionService.getLatestByTable(tableId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CLIENT','INVITED', 'SUPER_ADMIN','ROOT')")
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
    public ResponseEntity<TableSessionResponseDto> addClientToSession(
            @Parameter(description = "UUID de la sesión", required = true)
            @PathVariable UUID id,
            @Parameter(description = "UUID del cliente a agregar", required = true)
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(tableSessionService.addClient(id, clientId));
    }


    @PreAuthorize("hasAnyRole('CLIENT','INVITED','STAFF','ADMIN','ROOT')")
    @GetMapping("/{id}/orders")
    public ResponseEntity<Page<OrderResponseDto>> getOrdersByTableSession(
            @Parameter(description = "UUID de la table session", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @RequestParam(required = false) OrderStatus status,
            @Parameter Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByTableSessionAndStatus(id, status, pageable));
    }

}
