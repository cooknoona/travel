package com.travel.service;

import com.travel.constant.LogDetail;
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

    /** Join method, Encoding with hash code but will need email verification later */
    public Boolean signUp(SignUpRequest signUpRequest) {
        User user = signUpRequest.toUserEntity(passwordEncoder);
        userRepository.save(user);
        return true;
    }

    /** Login method, check accessibility of users to login first based on ip whether it's blocked or not.
     *  Also to double-check if one user attempts to log in without none existed userid then it'll block but with different reason
     *  inside try-catch, will verify with an authentication object and go through handleLoginSuccess to save user's event */
    @Transactional
    public LoginResponse login(LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        String ipAddress = servletRequest.getRemoteAddr();

        LogDetail blockReason = ipBlockService.getBlockReason(ipAddress);
        if (blockReason != LogDetail.NONE) {
            handleCatchBlockedIp(ipAddress, blockReason);
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
            log.error("Invalid credentials, given password is wrong");
            throw new UnauthenticatedException("Invalid credentials provided!");
        } catch (AuthenticationException e) {
            handleLoginFailure(ipAddress);
            log.error("Invalid authentication, given token is not valid");
            throw new UnauthenticatedException("User is not authenticated!");
        }
    }

    /** Helper method for login to investigate how the user got blocked to log-in */
    private void handleCatchBlockedIp(String ipAddress, LogDetail logDetail) {
        log.error("Blocked IP : {} login detected with reason : {}", ipAddress, logDetail.name());
        throw new LockedException("Login blocked due to: " + logDetail.name());
    }

    /** Helper method for login when none existent userid tries to access, and it's counting with findBy method query */
    private void handleNonexistentUser(String ipAddress) {
        ipBlockService.increaseLoginFailWithoutUserIdCount(ipAddress);

        if (ipBlockService.getBlockReason(ipAddress) == LogDetail.NON_EXISTENT_USER_ATTEMPTS) {
            ipBlockService.blockIp(ipAddress);
            log.error("Blocked IP : {}, with none existent userid accessed, and reason : {}", ipAddress, ipBlockService.getBlockReason(ipAddress));
            throw new LockedException("Too many login attempts with invalid userid, try again later!");
        }

        log.warn("Accessed IP : {}, with none existent userid has failed to login!", ipAddress);
        throw new ResourceNotFoundException("User not found");
    }

    /** Helper method for login when login has been successful and collect an event and log.
     *  Also, return access token and refresh token in http body and cookie each */
    private LoginResponse handleLoginSuccess(CustomUserDetails userDetails, String ipAddress, HttpServletResponse response) {
        Long userPk = userDetails.id();
        String nickname = userDetails.nickname();

        ipBlockService.resetLoginFailCount(ipAddress);

        String refreshToken = jwtUtility.addRefreshTokenToCookieAndReturn(userDetails, response);
        tokenService.saveRefreshToken(userPk, refreshToken);
        String accessToken = jwtUtility.generateAccessToken(userDetails);

        eventTrackingService.loginEventCollector(userPk, ipAddress, LogType.LOGIN);
        log.info("PK: {}'s login has been successful with IP: {}", userPk, ipAddress);
        return LoginResponse.ofAccessTokenAndUserInfo(accessToken, userPk, nickname);
    }

    /** Helper method for login when login process simply fails with wrong password and token */
    private void handleLoginFailure(String ipAddress) {
        ipBlockService.increaseLoginFailCount(ipAddress);
    }

    /** Re-issuing access token method with refresh tokens stores into cookie via HTTP headers */
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

    /** Logout method, first check the user authentication if the user on now has same information with authentication object
     *  In the token service, refresh tokens and AccessToken will store in blacklist */
    public void logout(String accessToken, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new UnauthenticatedException("Invalid authentication context");
        }
        try {
            tokenService.deleteRefreshToken(userDetails.id());
            tokenService.blacklistAccessToken(accessToken);
            log.info("UserId : {} has logged out", userDetails.userId());
        } catch (AuthenticationException e) {
            log.error("UserId : {} has failed logging out", userDetails.userId());
            throw new UnauthenticatedException("Logout failed");
        } finally {
            jwtUtility.clearRefreshTokenCookie(response);
        }
    }
}
