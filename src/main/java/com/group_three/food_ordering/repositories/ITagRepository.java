package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITagRepository extends JpaRepository<Tag, Long> {
}
