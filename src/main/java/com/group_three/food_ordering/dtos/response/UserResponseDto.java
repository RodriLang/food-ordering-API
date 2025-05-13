package com.group_three.food_ordering.dtos.response;

import com.group_three.food_ordering.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserResponseDto {
    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private LocalDate birthDate;
    private String phone;
    private LocalDate createdAt;
    private LocalDate removedAt;
    private RoleType role;
}