package com.travel.utility;

import com.travel.exception.client.AccessDeniedException;
import com.travel.exception.client.UnauthenticatedException;
import lombok.extern.log4j.Log4j2;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@Log4j2
public class SecurityUtility {
    public static Long getIdOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("[SecurityUtil.getIdOrThrow] User not authenticated.");
            throw new UnauthenticatedException("Authentication Failed!");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            Long id = customUserDetails.id();
            if (id == null) {
                log.info("[SecurityUtil.getIdOrThrow] Can't find user PK.");
                throw new UnauthenticatedException("Can't find user PK!");
            }
            return id;
        } else {
            log.info("[SecurityUtil.getIdOrThrow] Not found user information.");
            throw new UnauthenticatedException("Can't find user information!");
        }
    }

    public static Long isAdminOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("[SecurityUtil.isAdminUserOrThrow] User not authenticated.");
            throw new UnauthenticatedException("Authentication Failed!");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            Long id = userDetails.id();
            if (id == null) {
                log.info("[SecurityUtil.isAdminUserOrThrow] Can't find user PK.");
                throw new UnauthenticatedException("Can't find user PK!");
            }

            boolean isAdmin = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch("ADMIN"::equals);

            if (!isAdmin) {
                log.info("[SecurityUtil.isAdminUserOrThrow] No authority to access.");
                throw new AccessDeniedException("No Authority to access");
            }

            return id;
        } else {
            log.info("[SecurityUtil.isAdminUserOrThrow] Not found user information.");
            throw new UnauthenticatedException("Can't find user information!");
        }
    }

}
