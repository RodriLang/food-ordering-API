package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByParentCategoryIsNullAndDeletedFalse();

    List<Category> findByParentCategoryIdAndDeletedFalse(Long id);

    Optional<Category> findByIdAndDeletedFalse(Long id);

}
