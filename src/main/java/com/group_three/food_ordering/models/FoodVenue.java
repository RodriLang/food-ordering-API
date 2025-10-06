package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_venues")
@SQLDelete(sql = "UPDATE food_venues SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"products", "employees", "diningTables"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class FoodVenue extends BaseEntity {

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
