package com.group_three.food_ordering.dtos.update;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenueUpdateDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    private String name;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String address;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(min = 5, max = 50, message = "Email must be between 5 and 50 characters")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^(?:\\+54\\s?9?\\s?)?\\(?\\d{2,4}\\)?[\\s\\-]?\\d{3,4}[\\s\\-]?\\d{3,4}$",
            message = "The phone number must be a valid Argentine phone number"
    )
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @NotBlank(message = "Image URL is required")
    @Size(min = 5, max = 200, message = "Image URL must be between 5 and 200 characters")
    private String imageUrl;
}
