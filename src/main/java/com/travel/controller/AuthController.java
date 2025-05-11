package com.travel.controller;


import com.travel.dto.request.LoginRequest;
import com.travel.dto.request.SignUpRequest;
import com.travel.dto.response.LoginResponse;
import com.travel.dto.response.TokenResponse;
import com.travel.exception.client.UnauthenticatedException;
import com.travel.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(authService.join(signUpRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse tokens = authService.login(loginRequest, response);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/token/reissue")
    public ResponseEntity<TokenResponse> reIssueAccessToken(
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new UnauthenticatedException("Refresh token is missing");
        }
        return ResponseEntity.ok(authService.reIssueAccessToken(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.noContent().build();
    }
}
