package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String lastName;

    @Embedded
    private Address address;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime removedAt;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Employment> employments = new ArrayList<>();

    @Column
    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        if (deleted == null) deleted = false;
        if (this.id == null) this.id = UUID.randomUUID();
    }
}