package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderRepository extends JpaRepository<Order, UUID> {

        List<Order> findByVenueId(UUID venueId);

        Optional<Order> findByOrderIdAndVenueId(UUID orderId, UUID venueId);



    }
