/*package com.group_three.food_ordering.security;

import com.group_three.food_ordering.models.UserEntity;
import com.group_three.food_ordering.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("UserEntity not found: " + username));

        return new org.springframework.security.core.userdetails.UserEntity(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()  // suponiendo que tu entidad UserEntity implementa roles o authorities
        );
    }
}*/

