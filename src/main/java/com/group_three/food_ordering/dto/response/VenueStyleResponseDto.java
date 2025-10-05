package com.group_three.food_ordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VenueStyleResponseDto {

    private String logoUrl;

    private String bannerUrl;

    private String primaryColor;

    private String secondaryColor;

    private String accentColor;

    private String backgroundColor;

    private String textColor;

    private String slogan;

    private String description;

    private String instagramUrl;

    private String facebookUrl;

    private String whatsappNumber;

    private Boolean publicMenu;
}
