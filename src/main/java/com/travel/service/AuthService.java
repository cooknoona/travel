package com.travel.service;

import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.SignUpRequest;
import com.travel.dto.response.LoginResponse;
import com.travel.dto.response.TokenResponse;
import com.travel.entity.User;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.repository.UserRepository;
import com.travel.utility.CustomUserDetails;
import com.travel.utility.JwtUtility;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean join(SignUpRequest signUpRequest) {
        User user = signUpRequest.toUserEntity(passwordEncoder);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.userId(), request.password())
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long id = userDetails.id();
        String nickname = userDetails.nickname();
        String refreshToken = jwtUtility.addRefreshTokenToCookieAndReturn(userDetails, response);
        tokenService.saveRefreshToken(id, refreshToken);
        String accessToken = jwtUtility.generateAccessToken(userDetails);
        return LoginResponse.ofAccessTokenAndUserInfo(accessToken, id, nickname);
    }

    public TokenResponse reissue(String refreshToken) {
        jwtUtility.validateToken(refreshToken);
        Claims claims = jwtUtility.parseToken(refreshToken);
        Long userId = Long.valueOf(claims.getSubject());

        if (!tokenService.hasValidRefreshToken(userId, refreshToken)) {
            throw new UnauthenticatedException("Invalid refresh token");
        }

        String newAccessToken = jwtUtility.reIssueAccessToken(refreshToken).accessToken();
        return TokenResponse.ofAccessToken(newAccessToken);
    }

    public void logout(String accessToken, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            tokenService.deleteRefreshToken(userDetails.id());
            tokenService.blacklistAccessToken(accessToken);
        }

        jwtUtility.clearRefreshTokenCookie(response);
    }
}
