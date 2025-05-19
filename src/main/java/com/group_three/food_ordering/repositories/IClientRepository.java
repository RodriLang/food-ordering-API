package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClientRepository extends JpaRepository<Client, Long> {
}
