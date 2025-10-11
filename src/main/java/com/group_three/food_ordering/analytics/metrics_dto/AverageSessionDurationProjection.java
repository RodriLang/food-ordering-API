package com.group_three.food_ordering.analytics.metrics_dto;

public interface AverageSessionDurationProjection {

    String getVenueId();

    String getVenueName();

    Double getAverageSessionDurationMinutes();

}
