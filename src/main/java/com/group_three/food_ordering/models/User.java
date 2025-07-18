package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type")
@Table(name = "users")
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
    
    private LocalDateTime removedAt;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @PrePersist
    public void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
    }
}