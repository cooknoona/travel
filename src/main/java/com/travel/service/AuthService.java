package com.travel.service;

import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.SignUpRequest;
import com.travel.dto.response.LoginResponse;
import com.travel.dto.response.TokenResponse;
import com.travel.entity.User;
import com.travel.repository.UserRepository;
import com.travel.utility.CustomUserDetails;
import com.travel.utility.JwtUtility;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean join(SignUpRequest signUpRequest) {
        User user = signUpRequest.toUserEntity(passwordEncoder);
        userRepository.save(user);
        return true;
    }

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) {
        UsernamePasswordAuthenticationToken authenticationToken = loginRequest.toAuthentication();
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return jwtUtility.generateLoginTokens(userDetails, response);
    }

    public TokenResponse reIssueAccessToken(String refreshToken) {
        return jwtUtility.reIssueAccessToken(refreshToken);
    }

    public void logout(HttpServletResponse response) {
        jwtUtility.clearRefreshTokenCookie(response);
    }
}
