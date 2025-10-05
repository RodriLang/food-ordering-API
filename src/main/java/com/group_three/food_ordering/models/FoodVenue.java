package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "food_venues")
@SQLDelete(sql = "UPDATE food_venues SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"products", "employees", "diningTables"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FoodVenue extends BaseEntity {

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "public_id", length = 36, unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID publicId;

    @Column(length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String email;

    @Embedded
    private Address address;

    @Column(nullable = false, length = 20)
    private String phone;

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Employment> employees = new ArrayList<>();

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "foodVenue", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DiningTable> diningTables = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "venue_style_id")
    private VenueStyle venueStyle;

}
