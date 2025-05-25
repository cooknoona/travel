package com.travel.controller;


import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.SignUpRequest;
import com.travel.dto.response.LoginResponse;
import com.travel.dto.response.TokenResponse;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.service.AuthService;
import com.travel.utility.CustomUserDetails;
import com.travel.utility.JwtUtility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtility jwtUtility;

    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.join(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        LoginResponse tokens = authService.login(request, servletRequest ,servletResponse);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@CookieValue("refresh_token") String refreshToken) {
        TokenResponse newAccessToken = authService.accessTokenReissue(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractAccessTokenFromHeader(request);
        authService.logout(accessToken, response);
        return ResponseEntity.noContent().build();
    }

    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후 문자열 추출
        }
        throw new UnauthenticatedException("Access token is missing or invalid");
    }
}
