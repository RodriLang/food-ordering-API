package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.dto.request.FoodVenueCreateDto;
import com.group_three.food_ordering.dto.response.FoodVenueResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FoodVenueMapper {

    FoodVenue toEntity(FoodVenueCreateDto foodVenueDto);
    FoodVenueResponseDto toDTO(FoodVenue foodVenue);
}

