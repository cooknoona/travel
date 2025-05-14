package com.travel.service;

import com.travel.constant.LogDetail;
import com.travel.constant.LogStatus;
import com.travel.constant.LogType;
import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.SignUpRequest;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        // 차단 여부 확인
        LogDetail blockReason = ipBlockService.getBlockReason(ipAddress);
        if (blockReason != LogDetail.NONE) {
            handleBlockedIp(ipAddress, blockReason);
        }

        // 유저 존재 여부 확인
        boolean userExists = userRepository.existsByUserId(request.userId());
        if (!userExists) {
            handleNonexistentUser(ipAddress);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.userId(), request.password())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return handleLoginSuccess(userDetails, ipAddress, servletResponse);

        } catch (BadCredentialsException e) {
            handleLoginFailure(ipAddress);
            throw new UnauthenticatedException("Invalid credentials provided!");
        } catch (AuthenticationException e) {
            handleLoginFailure(ipAddress);
            throw new UnauthenticatedException("User is not authenticated!");
        }
    }

    private void handleBlockedIp(String ipAddress, LogDetail logDetail) {
        eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.BLOCKED);
        throw new LockedException("Login blocked due to: " + logDetail.name());
    }

    private void handleNonexistentUser(String ipAddress) {
        ipBlockService.increaseLoginFailWithoutUserIdCount(ipAddress);

        if (ipBlockService.getBlockReason(ipAddress) == LogDetail.NON_EXISTENT_USER_ATTEMPTS) {
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

    public TokenResponse accessTokenReissue(String refreshToken) {
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
