package com.group_three.food_ordering.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private final String email;
    private final String password; // o null si no lo usás en autenticación por JWT
    private final Collection<? extends GrantedAuthority> authorities;
    private final UUID participantId;
    private final UUID tableSessionId;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
