package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
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

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private Boolean deleted;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastUpdateDate;

    @ToString.Exclude
    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Table> tables = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.deleted == null) this.deleted = Boolean.FALSE;
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdateDate = LocalDateTime.now();
    }
}
