package com.group_three.food_ordering.analytics.metrics_repositories;

import com.group_three.food_ordering.analytics.metrics_dto.OrdersByVenueDto;
import com.group_three.food_ordering.analytics.metrics_dto.RevenueByVenueDto;
import com.group_three.food_ordering.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderMetricsRepository extends JpaRepository<Order, Long> {

    @Query("""
        SELECT new com.group_three.food_ordering.analytics.metrics_dto.OrdersByVenueDto(
            v.publicId,
            v.name,
            COUNT(o.id)
        )
        FROM Order o
        JOIN o.tableSession ts
        JOIN ts.diningTable dt
        JOIN dt.foodVenue v
        WHERE o.orderDate BETWEEN :from AND :to
        GROUP BY v.publicId, v.name
    """)
    List<OrdersByVenueDto> getOrdersGroupedByVenue(LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT new com.group_three.food_ordering.analytics.metrics_dto.RevenueByVenueDto(
            v.publicId,
            v.name,
            SUM(o.totalPrice),
            AVG(o.totalPrice)
        )
        FROM Order o
        JOIN o.tableSession ts
        JOIN ts.diningTable dt
        JOIN dt.foodVenue v
        WHERE o.status = 'COMPLETED'
          AND o.orderDate BETWEEN :from AND :to
        GROUP BY v.publicId, v.name
    """)
    List<RevenueByVenueDto> getRevenueGroupedByVenue(LocalDateTime from, LocalDateTime to);

    @Query("""
        SELECT SUM(o.totalPrice)
        FROM Order o
        JOIN o.tableSession ts
        JOIN ts.diningTable dt
        JOIN dt.foodVenue v
        WHERE v.publicId = :venueId
          AND o.status = 'COMPLETED'
          AND o.orderDate BETWEEN :from AND :to
    """)
    Double sumTotalRevenueByVenue(UUID venueId, LocalDateTime from, LocalDateTime to);

    // Cantidad de pedidos entre fechas
    @Query("""
        SELECT COUNT(o)
        FROM Order o
        WHERE o.orderDate BETWEEN :from AND :to
    """)
    long countOrdersBetween(LocalDateTime from, LocalDateTime to);

    // Cantidad de venues distintos con pedidos entre fechas
    @Query("""
        SELECT COUNT(DISTINCT dt.foodVenue.id)
        FROM Order o
        JOIN o.tableSession ts
        JOIN ts.diningTable dt
        WHERE o.orderDate BETWEEN :from AND :to
    """)
    long countDistinctVenuesBetween(LocalDateTime from, LocalDateTime to);


    // Porcentaje de pedidos cancelados en un local entre fechas
    @Query("""
    SELECT
        CASE WHEN COUNT(o) = 0 THEN 0
             ELSE (COUNT(CASE WHEN o.status = 'CANCELLED' THEN 1 END) * 100.0 / COUNT(o))
        END
    FROM Order o
    JOIN o.tableSession ts
    JOIN ts.diningTable dt
    WHERE dt.foodVenue.publicId = :venueId
      AND o.orderDate BETWEEN :from AND :to
""")
    double calculateCancellationRate(UUID venueId, LocalDateTime from, LocalDateTime to);


    // Métodos adicionales para venue metrics
    @Query("SELECT COUNT(o) FROM Order o JOIN o.tableSession ts JOIN ts.diningTable dt JOIN dt.foodVenue v WHERE v.publicId = :venueId AND o.orderDate BETWEEN :from AND :to")
    long countByVenueAndDateBetween(UUID venueId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT AVG(o.totalPrice) FROM Order o JOIN o.tableSession ts JOIN ts.diningTable dt JOIN dt.foodVenue v WHERE v.publicId = :venueId AND o.orderDate BETWEEN :from AND :to")
    double calculateAverageTicketByVenue(UUID venueId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT v.name FROM DiningTable dt JOIN dt.foodVenue v WHERE v.publicId = :venueId")
    String findVenueNameById(UUID venueId);

}
