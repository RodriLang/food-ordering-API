package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Surname must be between 3 and 50 characters")
    private String lastName;

    @Valid
    @NotNull(message = "Address is required")
    private AddressCreateDto address;

    @Email(message = "The email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @Past(message = "The date of birth must be in the past")
    @NotNull(message = "The birthdate is required") // este reemplaza NotBlank que no aplica a LocalDate
    private LocalDate birthDate;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^(?:\\+54\\s?9?\\s?)?\\(?\\d{2,4}\\)?[\\s\\-]?\\d{3,4}[\\s\\-]?\\d{3,4}$",
            message = "The phone number must be a valid Argentine phone number"
    )
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    private RoleType role;

}