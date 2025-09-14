package com.group_three.food_ordering.dto.response;

import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.models.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserResponseDto {
    private UUID id;
    private String name;
    private String lastName;
    private Address address;
    private String email;
    private LocalDate birthDate;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime removedAt;
    private RoleType role;
}