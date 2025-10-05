package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.VenueStyleRequestDto;
import com.group_three.food_ordering.dto.response.VenueStyleResponseDto;
import com.group_three.food_ordering.models.VenueStyle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface VenueStyleMapper {

    VenueStyle toEntity(VenueStyleRequestDto venueStyleRequestDto);

    @Mapping(target = "primaryColor", expression = "java(venueStyle.getColorsComplete() ? venueStyle.getPrimaryColor() : null)")
    @Mapping(target = "secondaryColor", expression = "java(venueStyle.getColorsComplete() ? venueStyle.getSecondaryColor() : null)")
    @Mapping(target = "accentColor", expression = "java(venueStyle.getColorsComplete() ? venueStyle.getAccentColor() : null)")
    @Mapping(target = "backgroundColor", expression = "java(venueStyle.getColorsComplete() ? venueStyle.getBackgroundColor() : null)")
    @Mapping(target = "textColor", expression = "java(venueStyle.getColorsComplete() ? venueStyle.getTextColor() : null)")
    VenueStyleResponseDto toDto(VenueStyle venueStyle);

}
