package com.travel.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
/** Exceptions such as BadCredentialException and AuthenticationException are same HTTP ERROR CODE 401.
 *  However, these two exceptions can't be handled by globalExceptionHandler. Because these two are basically via
 *  Spring Security Filter Chain. Different layer protection.
 *  More directly, it used JwtFilter to check if token validate(Authentication) or has values(BadCredentials) */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        String errorMessage = "Invalid username or password. Please check your credentials and try again.";

        ErrorResponse error = new ErrorResponse(
                401,
                "Unauthorized",
                List.of(errorMessage)
        );

        String json = new ObjectMapper().writeValueAsString(error);
        response.getWriter().write(json);
    }
}