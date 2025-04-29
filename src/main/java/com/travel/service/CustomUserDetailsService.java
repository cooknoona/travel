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

@Service
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String employeeCode) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(employeeCode)
                .orElseThrow(() -> new UsernameNotFoundException("Employee code not found"));
        return new CustomUserDetails(
                user.getId(),
                user.getUserId(),
                user.getName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthority().toString()))
        );
    }
}
