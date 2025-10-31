package com.group_three.food_ordering.services;

import com.group_three.food_ordering.enums.EmploymentStatus;
import com.group_three.food_ordering.models.Employment;

public interface EmploymentInvitationService {

    void createInvitation(Employment employment);

    String processResponse(String token, EmploymentStatus responseStatus);

}