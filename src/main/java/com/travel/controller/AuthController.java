package com.travel.controller;

import com.travel.dto.request.SignUpRequest;
import com.travel.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Boolean> signup(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(userService.join(signUpRequest));
    }
}
