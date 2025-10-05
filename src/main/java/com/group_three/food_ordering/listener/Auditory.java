package com.group_three.food_ordering.listener;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Embeddable
@Getter
@Setter
public class Auditory {

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "last_update_date")
    private LocalDateTime lastUpdateDate;

    @Column(name = "created_by")
    private UUID createdById;

    @Column(name = "last_updated_by")
    private UUID lastUpdatedById;
}