package com.travel.controller;


import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.SignUpRequest;
import com.travel.dto.response.LoginResponse;
import com.travel.dto.response.TokenResponse;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    /** Controller to sign-up, used @Valid annotation to pre-check format of user information to sign up */
    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.signUp(signUpRequest));
    }

    /** Controller to login, used @Valid annotation to pre-check format of id and password to login */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        LoginResponse tokens = authService.login(request, servletRequest ,servletResponse);
        return ResponseEntity.ok(tokens);
    }

    /** Controller to re-issue an access token, used Cookie only to have a safe packaging for refresh token not to expose refresh token */
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponse> reissue(@CookieValue("refresh_token") String refreshToken) {
        TokenResponse newAccessToken = authService.accessTokenReissue(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }

    /** Controller to log out, extracts the access token on HTTP header to double-check if the user and info match */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = extractAccessTokenFromHeader(request);
        authService.logout(accessToken, response);
        return ResponseEntity.noContent().build();
    }

    /** Helper method to extract the access token on HTTP header */
    private String extractAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 이후 문자열 추출
        }
        throw new UnauthenticatedException("Access token is missing or invalid");
    }
}
