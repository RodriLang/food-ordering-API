package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface FoodVenueMapper {

    FoodVenue toEntity(FoodVenueRequestDto foodVenueDto);

    FoodVenueAdminResponseDto toAdminDto(FoodVenue foodVenue);

    FoodVenuePublicResponseDto toPublicDto(FoodVenue foodVenue);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(FoodVenueRequestDto dto, @MappingTarget FoodVenue entity);

}

