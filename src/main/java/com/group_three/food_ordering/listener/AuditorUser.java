package com.group_three.food_ordering.listener;

import java.util.UUID;

public class AuditorUser {
    private UUID publicId;
    private String email;

    public AuditorUser(UUID publicId, String email) {
        this.publicId = publicId;
        this.email = email;
    }

    public UUID getPublicId() { return publicId; }
    public String getEmail() { return email; }
}
