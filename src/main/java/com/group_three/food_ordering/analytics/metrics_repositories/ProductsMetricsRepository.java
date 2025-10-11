package com.group_three.food_ordering.analytics.metrics_repositories;

import com.group_three.food_ordering.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductsMetricsRepository extends JpaRepository<Product, Long> {

    @Query(value = """
    SELECT p.public_id       AS productId,
           p.name            AS productName,
           COALESCE(SUM(od.quantity),0)                AS unitsSold,
           COALESCE(SUM(od.quantity * od.price),0) AS revenue
    FROM orders o
    JOIN order_details od ON od.order_id = o.id
    JOIN products p     ON p.id       = od.product_id
    WHERE o.order_date BETWEEN :from AND :to
      AND o.status IN (:status)
     AND o.food_venue_id = :venueId      -- opcional
    GROUP BY p.public_id, p.name
    ORDER BY revenue DESC
    LIMIT :limit
  """, nativeQuery = true)
    List<Map<String,Object>> topProducts(LocalDateTime from, LocalDateTime to, List<String> status, int limit, UUID venueId);
}