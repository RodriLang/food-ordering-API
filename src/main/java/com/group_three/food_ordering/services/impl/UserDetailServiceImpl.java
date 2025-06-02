package com.group_three.food_ordering.services.impl;


import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.repositories.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity =  userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se ha encontrado el usuario: " + username));

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userEntity.getRole().name());
        List<GrantedAuthority> authorities = List.of(authority);

        return new org.springframework.security.core.userdetails.User(userEntity.getEmail(),
                userEntity.getPassword(),
                true,
                true,
                true,
                true,
                authorities);
    }
}
