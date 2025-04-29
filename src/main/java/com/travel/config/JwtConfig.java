package com.travel.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtConfig {
    // Set the secret key from application properties
    @Value("${jwt.secret.key}")
    private String secretKey;
}
