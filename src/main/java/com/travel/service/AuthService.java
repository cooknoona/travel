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
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Log4j2
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

        LogDetail blockReason = ipBlockService.getBlockReason(ipAddress);
        if (blockReason != LogDetail.NONE) {
            handleBlockedIp(ipAddress, blockReason);
        }

        boolean userExists = userRepository.existsByUserId(request.userId());
        if (!userExists) {
            handleNonexistentUser(ipAddress);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(request.toAuthentication());

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return handleLoginSuccess(userDetails, ipAddress, servletResponse);

        } catch (BadCredentialsException e) {
            handleLoginFailure(ipAddress);
            log.error("Invalid credentials, check userId and/or password");
            throw new UnauthenticatedException("Invalid credentials provided!");
        } catch (AuthenticationException e) {
            handleLoginFailure(ipAddress);
            log.error("Invalid authentication, check access token");
            throw new UnauthenticatedException("User is not authenticated!");
        }
    }

    /** Helper method when blocked IP with existent userId logged in */
    private void handleBlockedIp(String ipAddress, LogDetail logDetail) {
        eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.BLOCKED);
        log.error("Blocked IP : {}, with existent userid accessed, and reason : {}", ipAddress, logDetail.name());
        throw new LockedException("Login blocked due to: " + logDetail.name());
    }

    private void handleNonexistentUser(String ipAddress) {
        ipBlockService.increaseLoginFailWithoutUserIdCount(ipAddress);

        if (ipBlockService.getBlockReason(ipAddress) == LogDetail.NON_EXISTENT_USER_ATTEMPTS) {
            ipBlockService.blockIp(ipAddress);
            eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.BLOCKED);
            log.error("Blocked IP : {}, with none existent userid accessed, and reason : {}", ipAddress, ipBlockService.getBlockReason(ipAddress));
            throw new LockedException("Too many login attempts with invalid userid, try again later!");
        }

        eventTrackingService.loginEventCollector(null, ipAddress, LogType.LOGIN, LogStatus.FAILED);
        log.warn("Accessed IP : {}, with none existent userid has failed to login!", ipAddress);
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
        log.info("PK: {}'s login has been successful with IP: {}", userPk, ipAddress);
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
