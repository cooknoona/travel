package com.travel.utility;

import com.travel.config.JwtConfig;
import com.travel.dto.response.TokenResponse;
import com.travel.entity.User;
import com.travel.exception.client.ResourceNotFoundException;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

@Slf4j
@Component
public class JwtUtility {
    private final String secretKey;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60; // 1 Day
    private static final long REFRESH_TOKEN_EXPIRATION = 60 * 60 * 1000 * 24 * 7;   // 7 Days
    private final UserRepository userRepository;

    /** Create constructor parameter to inject dependency
     * So @RequiredArgsConstructor can't call constructor with parameter fields */
    public JwtUtility(JwtConfig jwtConfig, UserRepository userRepository) {
        this.secretKey = jwtConfig.getSecretKey();
        this.userRepository = userRepository;
    }

    /** To get the Object of authenticated user via CustomUserDetails */
    public TokenResponse getPrincipal(Authentication authentication, HttpServletResponse httpServletResponse) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return generateToken(customUserDetails, httpServletResponse);
    }

    /** To generate access token and refresh token
     * And AccessToken, RefreshToken compacting */
    private TokenResponse generateToken(CustomUserDetails customUserDetails, HttpServletResponse httpServletResponse) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRATION);
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRATION);

        String accessToken = Jwts.builder()
                .subject(String.valueOf(customUserDetails.id()))
                .claim("name", customUserDetails.firstName())
                .claim("authorities", customUserDetails.authorities())
                .issuedAt(accessTokenExpiresIn)
                .signWith(key)
                .compact();

        String refreshToken = Jwts.builder()
                .subject(String.valueOf(customUserDetails.id()))
                .claim("name", customUserDetails.firstName())
                .claim("authorities", customUserDetails.authorities())
                .issuedAt(refreshTokenExpiresIn)
                .signWith(key)
                .compact();

        return TokenResponse.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /** Re-Issue access token */
    public String reIssueAccessToken(Authentication authentication, HttpServletResponse httpServletResponse) {
        return getPrincipal(authentication, httpServletResponse).getAccessToken();
    }

    /** Parsing token before authentication */
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes());
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** To create authenticate object to compare */
    public Authentication getAuthentication(String token) {
        Claims claims = parseToken(token);

        String primaryKey = claims.getSubject();
        User user = userRepository.findById(Long.valueOf(primaryKey))
                .orElseThrow(() -> new ResourceNotFoundException("User PK not found!"));
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                        claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        CustomUserDetails customUserDetails = new CustomUserDetails(
                user.getId(),
                user.getUserId(),
                user.getFirstName(),
                user.getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.getAuthority().toString()))
        );
        return new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
    }

    public boolean TokenValidation(String token) {
        try {
            parseToken(token);
            log.info("JWT Validated Successfully!");
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT Validation Failed!");
            throw new UnauthenticatedException("Invalid Token");
        }
    }
}
