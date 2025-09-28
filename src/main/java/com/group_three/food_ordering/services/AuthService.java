package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.security.LoginResponse;

import java.util.Optional;

public interface AuthService {


    LoginResponse login(LoginRequest loginRequest);

    Optional<User> getCurrentUser();

    Optional<Participant> getCurrentParticipant();

    TableSession getCurrentTableSession();

}