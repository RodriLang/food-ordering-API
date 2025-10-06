package com.group_three.food_ordering.dto;

import java.util.UUID;

public record AuditorUser(

        UUID publicId,

        String email

) {
}
