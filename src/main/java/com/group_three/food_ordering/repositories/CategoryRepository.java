package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category>findByParentCategoryIsNull();
    List<Category>findByParentCategoryId(Long id);
}
