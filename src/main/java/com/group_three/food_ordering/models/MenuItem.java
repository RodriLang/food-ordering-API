package com.group_three.food_ordering.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "menu_items")
public class MenuItem {

    @Id
    private Long id;
}
