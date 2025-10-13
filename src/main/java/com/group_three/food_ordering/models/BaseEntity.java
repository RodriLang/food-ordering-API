package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "public_id", length = 36, unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID publicId;

    @CreatedDate
    @Column(name = "creation_date", updatable = false)
    private Instant creationDate;

    @LastModifiedDate
    @Column(name = "last_update_date")
    private Instant lastUpdateDate;

    @CreatedBy
    @Column(name = "created_by", length = 36, updatable = false)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID createdById;

    @LastModifiedBy
    @Column(name = "last_updated_by", length = 36)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID lastUpdatedById;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @PrePersist
    void onCreate(){
        deleted = false;
        publicId = UUID.randomUUID();
    }
}
