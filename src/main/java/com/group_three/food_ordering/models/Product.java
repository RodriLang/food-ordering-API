package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "products")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_venue_id")
    private FoodVenue foodVenue;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private BigDecimal price;

    @Column
    private String imageUrl;

    @Column
    private Boolean available;

    @Column
    private Integer stock;

    @Column
    private Boolean deleted;

    @ManyToMany
    @JoinTable(
            name = "products_tags",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @PrePersist
    public void onCreate() {
        this.available = true;
        if (this.price == null) this.price = BigDecimal.ZERO;
        if (this.stock == null) this.stock = 0;
        if (this.deleted == null) this.deleted = false;
    }
}
