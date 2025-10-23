package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.models.Address;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, VenueStyleMapper.class, DiningTableMapper.class})
public interface FoodVenueMapper {

    @Mapping(target = "venueStyle", source = "styleRequestDto")
    @Mapping(target = "diningTables", source = "tables")
    FoodVenue toEntity(FoodVenueRequestDto foodVenueDto);

    FoodVenueAdminResponseDto toAdminDto(FoodVenue foodVenue);

    @Mapping(target = "styles", source = "venueStyle")
    @Mapping(source = "address", target = "address", qualifiedByName = "flatAddress")
    @Mapping(source = "address", target = "location", qualifiedByName = "flatLocation")
    FoodVenuePublicResponseDto toPublicDto(FoodVenue foodVenue);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "venueStyle", source = "styleRequestDto")
    @Mapping(target = "diningTables", source = "tables")
    void updateEntity(FoodVenueRequestDto dto, @MappingTarget FoodVenue entity);

    @Named("flatAddress")
    default String flatAddress(Address address) {
        return address.getStreet() + " " + address.getNumber();
    }

    @Named("flatLocation")
    default String flatLocation(Address address) {
        return address.getCity() + ", " + address.getCountry();
    }
}