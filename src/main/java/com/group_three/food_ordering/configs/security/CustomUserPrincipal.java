package com.group_three.food_ordering.configs.security;

import com.group_three.food_ordering.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CustomUserPrincipal implements UserDetails {

    private final UUID userId;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final RoleType role;
    private final UUID participantId;
    private final UUID tableSessionId;
    private final UUID foodVenueId;

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

    @Override
    public String getPassword() {
        return null;
    }
}
