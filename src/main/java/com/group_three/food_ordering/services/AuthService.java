package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;

public interface AuthService {


     AuthResponse login(LoginRequest loginRequest);

     String getCurrentEmail();

     User getCurrentUser();

     Client getCurrentClient();

     TableSession getCurrentTableSession();

}