package com.group_three.food_ordering.models;

import com.group_three.food_ordering.listener.Auditable;
import com.group_three.food_ordering.listener.Auditory;
import com.group_three.food_ordering.listener.AuditoryListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditoryListener.class)
@Getter
@Setter
public abstract class BaseEntity implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "creationDate", column = @Column(name = "creation_date", updatable = false)),
            @AttributeOverride(name = "lastUpdateDate", column = @Column(name = "last_update_date")),
            @AttributeOverride(name = "createdById", column = @Column(name = "created_by")),
            @AttributeOverride(name = "lastUpdatedById", column = @Column(name = "last_updated_by"))
    })
    private Auditory auditory = new Auditory();

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Override
    public Auditory getAuditory() {
        return auditory;
    }
}

