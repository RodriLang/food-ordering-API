package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByLabel(String name);

}
