package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.listener.AuditorUser;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.dto.response.LoginResponse;

import java.util.Optional;

public interface AuthService {

    AuditorUser getAuditorUser();

    LoginResponse login(LoginRequest loginRequest);

    void logout(String refreshToken);

    AuthResponse refreshAccessToken(RefreshTokenRequest request);

    Optional<User> getAuthUser();

    Optional<Participant> getCurrentParticipant();

    Optional<TableSession> getCurrentTableSession();

    User determineAuthUser();

    Participant determineCurrentParticipant();

    TableSession determineCurrentTableSession();

    RoleType getCurrentParticipantRole();

}