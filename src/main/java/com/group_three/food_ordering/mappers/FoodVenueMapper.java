package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.dtos.create.FoodVenueCreateDto;
import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FoodVenueMapper {

    FoodVenue toEntity(FoodVenueCreateDto foodVenueDto);
    FoodVenueResponseDto toDTO(FoodVenue foodVenue);
}
