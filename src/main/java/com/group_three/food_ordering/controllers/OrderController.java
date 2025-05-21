package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.OrderRequestDto;
import com.group_three.food_ordering.dtos.response.OrderResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.interfaces.IOrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.ORDER_BASE)
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody @Valid OrderRequestDto order) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(order));
    }


    @GetMapping()
    public ResponseEntity<List<OrderResponseDto>> getOrdersByPeriodAndStatus(
            @RequestParam LocalDate from,
            @RequestParam LocalDate to,
            @RequestParam OrderStatus status){
        return ResponseEntity.ok(null);

    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/today")
    public ResponseEntity<List<OrderResponseDto>> getDailyOrders() {
        return ResponseEntity.ok(orderService.getDailyOrders());
    }

    @GetMapping("/{foodVenueId}/{orderNumber}")
    public ResponseEntity<OrderResponseDto> getDailyOrderByOrderNumber(
            @PathVariable UUID foodVenueId,
            @PathVariable Integer orderNumber) {
        return ResponseEntity.ok(orderService.getDailyOrderByOrderNumber(foodVenueId, orderNumber));
    }

    @PatchMapping("/{id}/requirements")
    public ResponseEntity<OrderResponseDto> updateOrderRequirements(
            @PathVariable UUID id,
            @RequestParam @Size(max = 255) String requirements) {
        return ResponseEntity.ok(orderService.updateSpecialRequirements(id, requirements));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable UUID id) {
        return ResponseEntity.ok(orderService.updateStatus(id, OrderStatus.CANCELLED));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDto> updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }


//------------------------------------------------------------------//




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