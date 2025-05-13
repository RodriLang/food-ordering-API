package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IMenuItemRepository extends JpaRepository<MenuItem, Long> {
}
