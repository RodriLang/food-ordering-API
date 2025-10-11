package com.group_three.food_ordering.analytics.metrics_controllers;

import com.group_three.food_ordering.analytics.enums.TimeBucket;
import com.group_three.food_ordering.analytics.metrics_dto.*;
import com.group_three.food_ordering.enums.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/v1/metrics")
@Tag(name = "Métricas de análisis para administración", description = "Endpoints para métricas del local propio")
public interface MetricsAdminController {

    // ---- MÉTRICAS POR LOCAL (ADMIN) ----

    @GetMapping("/food-venue/overview")
    @Operation(summary = "Resumen por local", description = "Obtiene métricas específicas de un local entre fechas")
    ResponseEntity<VenueMetricsResponseDto> getVenueOverview(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/food-venue/sales")
    @Operation(summary = "Evolución de ventas por local", description = "Obtiene la evolución de ventas por día, semana o mes")
    ResponseEntity<List<TemporalSalesDto>> getSalesEvolution(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Agrupación de fechas: day, week, month", example = "day") @RequestParam(defaultValue = "day") TimeBucket timeBucket,
            @Parameter(description = "Agrupación de fechas: day, week, month", example = "day") @RequestParam(defaultValue = "day") List<OrderStatus> status
    );

    @GetMapping("/food-venue/top-products")
    @Operation(summary = "Top productos por local", description = "Obtiene los productos más vendidos de un local")
    ResponseEntity<List<ProductSalesDto>> getTopProducts(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Cantidad máxima de productos a devolver", example = "5") @RequestParam(defaultValue = "5") int limit
    );
}
