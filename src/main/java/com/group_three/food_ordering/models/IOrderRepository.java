package com.group_three.food_ordering.models;

import com.group_three.food_ordering.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IOrderRepository extends JpaRepository<Order, UUID> {

    }
