package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDto {

    @NotBlank(message = "Name is required", groups = OnCreate.class)
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @NotBlank(message = "Last name is required", groups = OnCreate.class)
    @Size(min = 3, max = 50, message = "Surname must be between 3 and 50 characters", groups = {OnCreate.class, OnUpdate.class})
    private String lastName;

    @Valid
    private AddressRequestDto address;

    @NotBlank(message = "Email is required", groups = OnCreate.class)
    @Email(message = "The email must be valid", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "Password is required", groups = OnCreate.class)
    @Size(min = 8, message = "Password must be at least 8 characters", groups = {OnCreate.class, OnUpdate.class})
    private String password;

    @Past(message = "The date of birth must be in the past", groups = {OnCreate.class, OnUpdate.class})
    private LocalDate birthDate;

    @Pattern(
            regexp = "^(?:\\+54\\s?9?\\s?)?\\(?\\d{2,4}\\)?[\\s\\-]?\\d{3,4}[\\s\\-]?\\d{3,4}$",
            message = "The phone number must be a valid Argentine phone number",
            groups = {OnCreate.class, OnUpdate.class}
    )
    @Size(max = 20, message = "Phone number cannot exceed 20 characters", groups = {OnCreate.class, OnUpdate.class})
    private String phone;

}