package com.travel.utility;


import com.travel.exception.client.BadRequestException;
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
    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest httpServletRequest,
                                    @Nullable HttpServletResponse httpServletResponse,
                                    @Nullable FilterChain filterChain)
            throws ServletException, IOException {
        if (httpServletRequest == null) {
            log.error("HttpServletRequest cannot be null!");
            throw new BadRequestException("HttpServletRequest cannot be null!");
        }
        if (httpServletResponse == null) {
            log.error("HttpServletResponse cannot be null!");
            throw new BadRequestException("HttpServletResponse cannot be null!");
        }
        if (filterChain == null) {
            log.error("FilterChain cannot be null!");
            throw new BadRequestException("FilterChain cannot be null!");
        }
        String jwt = resolveToken(httpServletRequest);
        if (StringUtils.hasText(jwt) && jwtUtility.TokenValidation(jwt)) {
            Authentication authentication = jwtUtility.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /** Extract Token from Authorization header */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
