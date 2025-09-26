package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;

import java.util.UUID;

public interface AuthService {


    AuthResponse login(LoginRequest loginRequest);

    AuthResponse initTableSession(User user, UUID foodVenueId, UUID tableSessionId);

    String getCurrentEmail();

    User getCurrentUser();

    Client getCurrentClient();

    TableSession getCurrentTableSession();

}