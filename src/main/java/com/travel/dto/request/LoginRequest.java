package com.travel.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public record LoginRequest(
        @NotBlank(message = "ID is required")
        @Size(min = 4, max = 20, message = "ID length must be between 4 - 20")
        @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "ID must include letters, numbers, and underscores only")
        String userId,

        @NotBlank(message = "PW is required")
        @Size(min = 8, max = 100, message = "Password length must be between 8 - 100")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
                message = "PW must be at least 8 characters long and include letters, numbers, and special characters")
        String password
) {
    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(userId, password);
    }
}
