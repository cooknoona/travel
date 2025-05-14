package com.travel.service;

import com.travel.constant.LogStatus;
import com.travel.constant.LogType;
import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.SignUpRequest;
import com.travel.dto.request.UserEventLogRequest;
import com.travel.dto.response.LoginResponse;
import com.travel.dto.response.TokenResponse;
import com.travel.entity.User;
import com.travel.exception.client.LockedException;
import com.travel.exception.client.ResourceNotFoundException;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.repository.UserRepository;
import com.travel.utility.CustomUserDetails;
import com.travel.utility.JwtUtility;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final EventTrackingService eventTrackingService;
    private final IpBlockService ipBlockService;
    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Boolean join(SignUpRequest signUpRequest) {
        User user = signUpRequest.toUserEntity(passwordEncoder);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String ipAddress = servletRequest.getRemoteAddr();

        if (ipBlockService.isBlocked(ipAddress)) {
            handleBlockedIp(ipAddress);
        }

        boolean userExists = userRepository.existsByUserId(request.userId());
        if (!userExists) {
            handleNonexistentUser(request.userId(), ipAddress);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.userId(), request.password())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return handleLoginSuccess(userDetails, ipAddress, servletResponse);
        } catch (BadCredentialsException | UnauthenticatedException e) {
            handleLoginFailure(ipAddress);
            throw e instanceof BadCredentialsException
                    ? new UnauthenticatedException("Invalid credentials")
                    : e;
        }
    }

    private void handleBlockedIp(String ipAddress) {
        eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.BLOCKED);
        throw new LockedException("Too many failed login attempts. Try again later!");
    }

    private void handleNonexistentUser(String userId, String ipAddress) {
        ipBlockService.increaseLoginFailWithoutUserIdCount(ipAddress);

        if (ipBlockService.isBlockedDueToNonexistentUsers(ipAddress)) {
            ipBlockService.blockIp(ipAddress);
            eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.BLOCKED);
            throw new LockedException("Too many login attempts with invalid user IDs. Try again later!");
        }

        eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.FAILED);
        throw new ResourceNotFoundException("User not found");
    }

    private LoginResponse handleLoginSuccess(CustomUserDetails userDetails, String ipAddress, HttpServletResponse response) {
        Long userPk = userDetails.id();
        String nickname = userDetails.nickname();

        ipBlockService.resetLoginFailCount(ipAddress);

        String refreshToken = jwtUtility.addRefreshTokenToCookieAndReturn(userDetails, response);
        tokenService.saveRefreshToken(userPk, refreshToken);
        String accessToken = jwtUtility.generateAccessToken(userDetails);

        eventTrackingService.loginEventCollector(userPk, ipAddress, LogType.LOGIN, LogStatus.SUCCEEDED);
        return LoginResponse.ofAccessTokenAndUserInfo(accessToken, userPk, nickname);
    }

    private void handleLoginFailure(String ipAddress) {
        ipBlockService.increaseLoginFailCount(ipAddress);
        eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.FAILED);
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
