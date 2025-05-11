package com.group_three.food_ordering.mappers;


import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.dtos.FoodVenueCreateDto;
import com.group_three.food_ordering.dtos.FoodVenueResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface IFoodVenueMapper {

    IFoodVenueMapper INSTANCE = Mappers.getMapper(IFoodVenueMapper.class);

    FoodVenueResponseDto toDTO(FoodVenueCreateDto foodVenueCreateDto);

    FoodVenueResponseDto toDTO(FoodVenue foodVenue);

    FoodVenue toEntity(FoodVenueResponseDto orderDTO);

    FoodVenue toEntity(FoodVenueCreateDto orderDTO);
}
