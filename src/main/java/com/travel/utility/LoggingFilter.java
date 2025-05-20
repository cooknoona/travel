package com.travel.utility;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        ThreadContext.put("traceId", UUID.randomUUID().toString());
        ThreadContext.put("ip", request.getRemoteAddr());
        ThreadContext.put("requestPath", request.getRequestURI());
        ThreadContext.put("httpMethod", request.getMethod());
        ThreadContext.put("userAgent", request.getHeader("User-Agent"));

        try {
            filterChain.doFilter(request, response);
        } finally {
            ThreadContext.clearAll();
        }
    }
}
