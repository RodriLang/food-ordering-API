package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "food_venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenue {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String address;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 200)
    private String imageUrl;

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> employees;

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus;

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Table> tables;

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TableSession> tableSessions;
}
