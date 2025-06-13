package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ORDER_BASE)
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @Operation(
            summary = "Crear una nueva orden",
            description = "Crea una orden con los datos proporcionados en el cuerpo de la solicitud."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos en la solicitud")
    })
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(order));
    }




    @Operation(
            summary = "Obtener órdenes con filtros opcionales",
            description = "Devuelve una lista de órdenes que pueden ser filtradas por rango de fechas y estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Órdenes recuperadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping()
    public ResponseEntity<List<OrderResponseDto>> getOrders(
            @Parameter(description = "Fecha desde la cual buscar órdenes (formato yyyy-MM-dd)", example = "2025-05-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @Parameter(description = "Fecha hasta la cual buscar órdenes (formato yyyy-MM-dd)", example = "2025-05-10")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

            @Parameter(description = "Estado de la orden para filtrar", example = "PENDING")
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(orderService.getOrdersByFilters(from, to, status));
    }



    @Operation(
            summary = "Obtener las órdenes del dia",
            description = "Devuelve una lista de órdenes del dia en curso que pueden ser filtralas por estado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Órdenes recuperadas exitosamente"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @GetMapping("/today")
    public ResponseEntity<List<OrderResponseDto>> getDailyOrders(
            @Parameter(description = "Estado de la orden para filtrar", example = "PENDING")
            @RequestParam(required = false) OrderStatus status) {

        return ResponseEntity.ok(orderService.getOrdersForToday(status));
    }



    @Operation(
            summary = "Obtener una orden por ID",
            description = "Devuelve los detalles de una orden específica identificada por su UUID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden encontrada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getById(id));
    }





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
    public ResponseEntity<OrderResponseDto> getOrderByDateAndOrderNumber(
            @Parameter(description = "Fecha de la orden en formato yyyy-MM-dd", example = "2025-05-25")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,

            @Parameter(description = "Número de orden del día", example = "15")
            @PathVariable Integer orderNumber) {

        return ResponseEntity.ok(orderService.getOrderByDateAndOrderNumber(date, orderNumber));
    }





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
    public ResponseEntity<OrderResponseDto> updateOrderRequirements(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,

            @Parameter(description = "Requisitos especiales o notas", example = "Sin cebolla, extra picante")
            @RequestParam @Size(max = 255) String requirements) {
        return ResponseEntity.ok(orderService.updateSpecialRequirements(id, requirements));
    }




    @Operation(
            summary = "Cancelar una orden",
            description = "Marca una orden como cancelada."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orden cancelada exitosamente"),
            @ApiResponse(responseCode = "404", description = "Orden no encontrada")
    })
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.updateStatus(id, OrderStatus.CANCELLED));
    }



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
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @Parameter(description = "UUID de la orden", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,

            @Parameter(description = "Nuevo estado de la orden", example = "COMPLETED")
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }



}




















/*
1. Crear, leer, actualizar, borrar (CRUD básico)
Método	Endpoint	Descripción
POST	/orders	Crear una nueva orden
GET	/orders/{id}	Obtener una orden específica del tenant
PUT	/orders/{id}	Actualizar una orden (ej: notas, estado si permitís, detalles)
DELETE	/orders/{id}	Eliminar lógicamente una orden (deleted = true)
2. Cambiar estado de orden (transiciones del workflow)
Método	Endpoint	Descripción
PATCH	/orders/{id}/status	Cambiar el estado de la orden (por ejemplo: SERVED, CANCELED, RETURNED, COMPLETED, etc.)

    Este endpoint debe recibir un body como:

{
  "newStatus": "CANCELED"
}

3. Búsquedas filtradas por fechas y estado
Método	Endpoint	Descripción
GET	/orders/today	Todas las órdenes del día actual del tenant
GET	/orders/today?status=	Órdenes del día filtradas por estado (PENDING, SERVED, etc.)
GET	/orders	Órdenes por rango de fechas y estado: /orders?from=2025-05-01&to=2025-05-10&status=COMPLETED

    También podrías admitir filtros como clientId, tableSessionId, etc.

4. Operaciones relacionadas con TableSession (uso frecuente en salón)
Método	Endpoint	Descripción
GET	/orders/by-table-session/{id}	Obtener todas las órdenes de una mesa activa (útil para mostrarle al mozo)
5. Consultas especiales (estadísticas o reportes)
Método	Endpoint	Descripción
GET	/orders/stats/today	Total de órdenes, ingresos, etc. del día
GET	/orders/stats/by-status	Contar órdenes agrupadas por estado (ej: cuántas PENDING, COMPLETED, CANCELED)
GET	/orders/stats/monthly	Total de ingresos por mes para gráficos o dashboards
6. Casos especiales
Método	Endpoint	Descripción
GET	/orders/pending	Todas las órdenes pendientes
GET	/orders/active	Órdenes activas (ej: PENDING, IN_PREPARATION) excluyendo completadas o canceladas
GET	/orders/returned	Órdenes devueltas por clientes
GET	/orders/by-client/{id}	Todas las órdenes de un cliente específico


ENDPOINTS SUGERIDOS PARA OrderController
1. CRUD básico

GET    /orders                 -> Obtener todas las órdenes (paginadas)
GET    /orders/{id}           -> Obtener una orden por ID
POST   /orders                -> Crear nueva orden
PUT    /orders/{id}           -> Actualizar orden (si lo permitís)
DELETE /orders/{id}           -> Borrado lógico (soft delete con deleted=true)

2. Cambio de estado flexible

PATCH  /orders/{id}/status    -> Cambiar el estado de una orden
Body: { "status": "IN_PROGRESS" }

    Así evitás múltiples endpoints como /mark-as-ready, /cancel, etc.
    Validás la transición en el service.

3. Filtros de búsqueda por estado y fecha

GET /orders/status/{status}                     -> Órdenes por estado
GET /orders/today                               -> Órdenes del día actual
GET /orders/today?status=PENDING                -> Órdenes del día actual con estado específico
GET /orders/by-date?from=2025-05-01&to=2025-05-19   -> Órdenes entre fechas
GET /orders/month/2025-05                       -> Todas las de mayo 2025

4. Filtrados por cliente, local o mesa

GET /orders/client/{clientId}
GET /orders/venue/{venueId}
GET /orders/table-session/{sessionId}

5. Reportes o métricas (opcionales, pero útiles)

GET /orders/count-by-status              -> Total por estado (gráfico circular)
GET /orders/total-sales-by-day          -> Total recaudado por día
GET /orders/top-clients                 -> Clientes que más compran
GET /orders/average-order-value         -> Promedio por orden

6. Reclamos o devoluciones

Si usás estados como REJECTED o RETURNED, podés usar:

PATCH /orders/{id}/status
Body: { "status": "REJECTED", "reason": "Pedido frío" }

Y opcionalmente:

POST /orders/{id}/claim
Body: { "reason": "...", "refundRequested": true }

Resumen estructurado por utilidad
Categoría	Endpoints principales
CRUD básico	GET, POST, PUT, DELETE
Gestión de estado	PATCH /{id}/status
Filtros por estado	/status/{status}, /today
Fechas	/by-date, /month/yyyy-MM
Búsqueda por entidad	/client/{id}, /venue/{id}
Métricas/Reportes	/count-by-status, /top-clients
Devoluciones	/claim, cambio a REJECTED o RETURNED
 */