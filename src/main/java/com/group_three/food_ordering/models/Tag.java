package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "tags")
@SQLDelete(sql = "UPDATE tags SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column
    @EqualsAndHashCode.Include
    private String label;

}
