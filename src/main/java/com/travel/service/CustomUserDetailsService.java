package com.travel.service;

import com.travel.entity.User;
import com.travel.repository.UserRepository;
import com.travel.utility.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/** For Session managing, Will probably used later when login logics passing by foam style login architecture */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("userId not found"));
        return new CustomUserDetails(
                user.getId(),
                user.getUserId(),
                user.getNickName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthority().toString()))
        );
    }
}
