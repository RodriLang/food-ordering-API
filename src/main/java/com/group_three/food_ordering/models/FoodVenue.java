package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "food_venues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenue {
    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Embedded
    private Address address;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 200)
    private String imageUrl;

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Table> tables = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
    }
}
