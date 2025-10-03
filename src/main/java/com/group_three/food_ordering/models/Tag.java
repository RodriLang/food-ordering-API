package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity(name = "tags")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String label;

    @Column
    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        if (deleted == null) deleted = false;
    }
}
