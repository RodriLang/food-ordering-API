package com.group_three.food_ordering.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "food_venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenueEntity {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String imageUrl;
}
