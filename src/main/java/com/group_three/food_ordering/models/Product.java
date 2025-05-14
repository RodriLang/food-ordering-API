package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "food_venue_id")
    private FoodVenue foodVenue;

    @Column
    private String name;

    @Column(length = 255)
    private String description;

    @Column
    private BigDecimal price;

    @Column
    private String imageUrl;

    @Column
    private Boolean available;

    @Column
    private Integer stock;

    @ManyToMany
    @JoinTable(
            name = "products_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags= new ArrayList<>();

    @PrePersist
    public void onCreate()
    {
        this.available = true;
        if (this.price == null){ this.price = BigDecimal.ZERO;
        }
        if (this.stock == null){ this.stock = 0;}
    }

}
