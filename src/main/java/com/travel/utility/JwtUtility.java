package com.travel.utility;

import com.travel.config.WebConfig;
import com.travel.dto.response.TokenResponse;
import com.travel.entity.User;
import com.travel.exception.client.ResourceNotFoundException;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Log4j2
@Component
public class JwtUtility {
    private final String secretKey;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1Hour
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7Days

    private final UserRepository userRepository;

    public JwtUtility(WebConfig webConfig, UserRepository userRepository) {
        this.secretKey = webConfig.getSecretKey();
        this.userRepository = userRepository;
    }

    public String generateAccessToken(CustomUserDetails userDetails) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .subject(String.valueOf(userDetails.id()))
                .claim("userId", userDetails.userId())
                .claim("nickname", userDetails.nickname())
                .claim("authorities", userDetails.authorities())
                .issuedAt(new Date(now))
                .expiration(new Date(now + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public String addRefreshTokenToCookieAndReturn(CustomUserDetails userDetails, HttpServletResponse response) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        long now = System.currentTimeMillis();

        String refreshToken = Jwts.builder()
                .subject(String.valueOf(userDetails.id()))
                .issuedAt(new Date(now))
                .expiration(new Date(now + REFRESH_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();

        addRefreshTokenToCookie(response, refreshToken);
        return refreshToken;
    }

    public TokenResponse reIssueAccessToken(String refreshToken) {
        Claims claims = parseToken(refreshToken);
        String id = claims.getSubject();

        User user = userRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(), user.getUserId(), user.getNickName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthority().toString()))
        );

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        long now = System.currentTimeMillis();

        String newAccessToken = Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("userId", userDetails.userId())
                .claim("nickname", user.getNickName())
                .claim("authorities", userDetails.authorities())
                .issuedAt(new Date(now))
                .expiration(new Date(now + ACCESS_TOKEN_EXPIRATION))
                .signWith(key)
                .compact();

        return TokenResponse.ofAccessToken(newAccessToken);
    }

    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);
        String userId = claims.getSubject();

        User user = userRepository.findById(Long.valueOf(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                        claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        CustomUserDetails userDetails = new CustomUserDetails(
                user.getId(), user.getUserId(), user.getFirstName(), user.getPassword(), authorities
        );
        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthenticatedException("Invalid JWT token");
        }
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(REFRESH_TOKEN_EXPIRATION / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public long getRemainingExpiration(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }
}