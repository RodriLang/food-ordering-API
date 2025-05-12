package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface IProductRepository extends JpaRepository<Product, Long> {
}
