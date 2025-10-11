package com.group_three.food_ordering.analytics.metrics_controllers;

import com.group_three.food_ordering.analytics.metrics_dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequestMapping("/api/v1/metrics")
@Tag(name = "Métricas de análisis generales para usuarios root", description = "Endpoints para métricas por local")
public interface MetricsRootController {

    // ---- MÉTRICAS GENERALES (ROOT) ----

    @GetMapping("/general/overview")
    @Operation(summary = "Resumen general de métricas", description = "Obtiene métricas generales entre fechas")
    ResponseEntity<GeneralMetricsResponseDto> getGeneralOverview(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/general/orders")
    @Operation(summary = "Pedidos por local", description = "Cantidad de pedidos por local entre fechas")
    ResponseEntity<List<OrdersByVenueDto>> getOrdersByVenue(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/general/revenue")
    @Operation(summary = "Ingresos por local", description = "Suma y promedio de ingresos por local entre fechas")
    ResponseEntity<List<RevenueByVenueDto>> getRevenueByVenue(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to
    );

    @GetMapping("/general/top-venues")
    @Operation(summary = "Top locales por ingresos", description = "Obtiene los locales con mayor ingreso en el rango de fechas")
    ResponseEntity<List<RevenueByVenueDto>> getTopVenuesByRevenue(
            @Parameter(description = "Fecha inicial", example = "2025-01-01T00:00:00") @RequestParam LocalDateTime from,
            @Parameter(description = "Fecha final", example = "2025-01-31T23:59:59") @RequestParam LocalDateTime to,
            @Parameter(description = "Cantidad máxima de locales a devolver", example = "5") @RequestParam(defaultValue = "5") int limit
    );

}
