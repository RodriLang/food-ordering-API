package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "venue_styles")
@SQLDelete(sql = "UPDATE venue_styles SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VenueStyle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 500)
    private String logoUrl;

    @Column(length = 500)
    private String bannerUrl;

    @Column(length = 7)
    private String primaryColor;

    @Column(length = 7)
    private String secondaryColor;

    @Column(length = 7)
    private String accentColor;

    @Column(length = 7)
    private String backgroundColor;

    @Column(length = 7)
    private String textColor;

    @Column
    private Boolean colorsComplete;

    @Column(length = 200)
    private String slogan;

    @Column(length = 1000)
    private String description;

    @Column
    private Boolean publicMenu;

    @Column(length = 200)
    private String instagramUrl;

    @Column(length = 200)
    private String facebookUrl;

    @Column(length = 200)
    private String whatsappNumber;

}