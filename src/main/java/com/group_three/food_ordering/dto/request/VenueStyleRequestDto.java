package com.group_three.food_ordering.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VenueStyleRequestDto {

    @NotNull(message = "Logo url is required", groups = OnCreate.class)
    @Size(max = 500, message = "Logo url must be 500 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String logoUrl;

    @NotNull(message = "Banner url is required", groups = OnCreate.class)
    @Size(max = 500, message = "Banner url must be 500 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String bannerUrl;

    @Size(min = 7, max = 7, message = "Primary color must be hexadecimal", groups = {OnCreate.class, OnUpdate.class})
    private String primaryColor;

    @Size(min = 7, max = 7, message = "Secondary color must be hexadecimal", groups = {OnCreate.class, OnUpdate.class})
    private String secondaryColor;

    @Size(min = 7, max = 7, message = "Accent color must be hexadecimal", groups = {OnCreate.class, OnUpdate.class})
    private String accentColor;

    @Size(min = 7, max = 7, message = "Background color must be hexadecimal", groups = {OnCreate.class, OnUpdate.class})
    private String backgroundColor;

    @Size(min = 7, max = 7, message = "Text color must be hexadecimal", groups = {OnCreate.class, OnUpdate.class})
    private String textColor;

    @Size(max = 200, message = "Slogan must be 200 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String slogan;

    @Size(max = 1000, message = "Description must be 1000 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @Size(max = 200, message = "Instagram url must be 200 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String instagramUrl;

    @Size(max = 200, message = "Facebook url must be 200 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String facebookUrl;

    @Size(max = 200, message = "Whatsapp number must be 200 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String whatsappNumber;

    @NotNull(message = "Public menu status is required", groups = OnCreate.class)
    private Boolean publicMenu;

}
