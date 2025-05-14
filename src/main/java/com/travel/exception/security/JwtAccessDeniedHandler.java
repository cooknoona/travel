package com.travel.exception.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.travel.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
/** JwtAccessDeniedHandler is used in Spring Security layer. In SecurityConfig class, when the user isn't allowed to
 *  access admin pages, will block and throw an exceptions. Same as Authentication exception, can't be called by
 *  GlobalExceptionHandler which is in the Business or service layer exception. */
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String errorMessage = "You have no authorities to access this!";

        ErrorResponse errorResponse = new ErrorResponse(
                403,
                "Forbidden",
                List.of(errorMessage)
        );

        String jsonResponse = new ObjectMapper().writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
    }
}
