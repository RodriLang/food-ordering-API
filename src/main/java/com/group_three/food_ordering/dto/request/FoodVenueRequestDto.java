package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.dto.create.*;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenueRequestDto {

    @NotBlank(message = "Name is required", groups = {OnCreate.class})
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @Valid
    private AddressCreateDto address;

    @NotBlank(message = "Email is required", groups = {OnCreate.class})
    @Email(message = "Email must be a valid email address", groups = {OnCreate.class, OnUpdate.class})
    private String email;

    @NotBlank(message = "Phone is required", groups = {OnCreate.class})
    @Pattern(
            regexp = "^(?:\\+54\\s?9?\\s?)?\\(?\\d{2,4}\\)?[\\s\\-]?\\d{3,4}[\\s\\-]?\\d{3,4}$",
            message = "The phone number must be a valid Argentine phone number",
            groups = {OnCreate.class, OnUpdate.class}
    )
    @Size(max = 20, message = "Phone number cannot exceed 20 characters", groups = {OnCreate.class, OnUpdate.class})
    private String phone;

    @NotBlank(message = "Image URL is required", groups = {OnCreate.class})
    @Size(min = 5, max = 200, message = "Image URL must be between 5 and 200 characters", groups = {OnCreate.class, OnUpdate.class})
    private String imageUrl;

    private List<EmploymentRequestDto> employees;

    private List<ProductRequestDto> products;

    private List<TableCreateDto> tables;

    private List<MenuCreateDto> menus;
}
