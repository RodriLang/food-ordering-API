package com.group_three.food_ordering.services;

import com.group_three.food_ordering.repositories.FoodVenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FoodVenueService {

    private final FoodVenueRepository foodVenueRepository;

    @Autowired
    public FoodVenueService(FoodVenueRepository foodVenueRepository) {
        this.foodVenueRepository = foodVenueRepository;
    }


}
