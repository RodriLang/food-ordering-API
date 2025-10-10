package com.group_three.food_ordering.analytics.metrics_controllers;

import com.group_three.food_ordering.analytics.metrics_dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/metrics")
@Tag(name = "Metrics", description = "Endpoints para métricas generales y por local")
public interface MetricsController {

    // ---- MÉTRICAS GENERALES (ROOT / ADMIN) ----

    @GetMapping("/general/overview")
    @Operation(summary = "Resumen general de métricas", description = "Obtiene métricas generales entre fechas")
    GeneralMetricsResponseDto getGeneralOverview(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/general/orders")
    @Operation(summary = "Pedidos por local", description = "Cantidad de pedidos por local entre fechas")
    List<OrdersByVenueDto> getOrdersByVenue(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/general/revenue")
    @Operation(summary = "Ingresos por local", description = "Suma y promedio de ingresos por local entre fechas")
    List<RevenueByVenueDto> getRevenueByVenue(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/general/top-venues")
    @Operation(summary = "Top locales por ingresos", description = "Obtiene los locales con mayor ingreso en el rango de fechas")
    List<RevenueByVenueDto> getTopVenuesByRevenue(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Cantidad máxima de locales a devolver", example = "5") @RequestParam(defaultValue = "5") int limit
    );


    // ---- MÉTRICAS POR LOCAL (CONTEXTUALES) ----

    @GetMapping("/venue/{venueId}/overview")
    @Operation(summary = "Resumen por local", description = "Obtiene métricas específicas de un local entre fechas")
    VenueMetricsResponseDto getVenueOverview(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/venue/{venueId}/sales")
    @Operation(summary = "Evolución de ventas por local", description = "Obtiene la evolución de ventas por día, semana o mes")
    List<TemporalSalesDto> getSalesEvolution(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Agrupación de fechas: day, week, month", example = "day") @RequestParam(defaultValue = "day") String groupBy
    );

    @GetMapping("/venue/{venueId}/top-products")
    @Operation(summary = "Top productos por local", description = "Obtiene los productos más vendidos de un local")
    List<ProductSalesDto> getTopProducts(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Cantidad máxima de productos a devolver", example = "5") @RequestParam(defaultValue = "5") int limit
    );

    @GetMapping("/venue/{venueId}/employees")
    @Operation(summary = "Rendimiento de empleados por local", description = "Obtiene métricas de desempeño de los empleados de un local")
    List<EmployeePerformanceDto> getEmployeePerformance(
            @Parameter(description = "ID del local") @PathVariable UUID venueId,
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );
}
