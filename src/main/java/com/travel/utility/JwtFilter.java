package com.travel.utility;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtility jwtUtility;
    @Override
    protected void doFilterInternal(@Nullable HttpServletRequest httpServletRequest,
                                    @Nullable HttpServletResponse httpServletResponse,
                                    @Nullable FilterChain filterChain)
            throws ServletException, IOException {
        if (httpServletRequest == null) {
            throw new ServletException("HttpServletRequest cannot be null!");
        }
        if (httpServletResponse == null) {
            throw new ServletException("HttpServletResponse cannot be null!");
        }
        if (filterChain == null) {
            throw new ServletException("FilterChain cannot be null!");
        }
        String jwt = resolveToken(httpServletRequest); // 헤더에서 JWT 추출
        if (StringUtils.hasText(jwt) && jwtUtility.TokenValidation(jwt)) {
            Authentication authentication = jwtUtility.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);   // To create authentication object
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse); // To deliver it to the next filter
    }

    /** Extract Token from Authorization header */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " remove
        }
        return null;
    }
}
