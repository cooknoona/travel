package com.travel.dto.request;

import lombok.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogInRequest {
    private String userId;
    private String password;

    public UsernamePasswordAuthenticationToken toAuthentication() {
        return new UsernamePasswordAuthenticationToken(userId, password);
    }
}
