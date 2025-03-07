package com.dhia.Upvertise.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ) {
            return Optional.empty();
        }

        // User userPrincipal = (User) authentication.getPrincipal();

        return Optional.of(authentication.getName()); // Returns the username of the logged-in user
    }
}
