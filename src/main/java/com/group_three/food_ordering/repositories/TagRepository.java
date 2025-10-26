package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByLabel(String name);

    Set<Tag> findAllByLabelIn(List<String> names);
}
