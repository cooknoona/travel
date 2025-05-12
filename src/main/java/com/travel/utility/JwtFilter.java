package com.travel.utility;


import com.travel.exception.client.BadRequestException;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtility jwtUtility;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest request,
                                    @Nullable HttpServletResponse response,
                                    @Nullable FilterChain filterChain) throws ServletException, IOException {
        if (request == null || response == null || filterChain == null) {
            throw new BadRequestException("Request, Response, or FilterChain is null");
        }

        try {
            String token = resolveToken(request);
            if (StringUtils.hasText(token)) {
                validateAndAuthenticateToken(token);
            }
            filterChain.doFilter(request, response);
        } catch (UnauthenticatedException e) {
            log.error("JWT validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void validateAndAuthenticateToken(String token) {
        if (jwtUtility.validateToken(token)) {
            if (tokenService.isBlacklisted(token)) {
                throw new UnauthenticatedException("Access token is blacklisted");
            }
            Authentication authentication = jwtUtility.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            throw new UnauthenticatedException("Invalid JWT token");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
