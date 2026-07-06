package com.nelly.hivtbmonitoringsystem.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    private SecurityUtil() {}

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    /**
     * True if the authenticated user holds any of the given roles (bare names,
     * e.g. "ADMIN"). Reads JWT authorities — no DB hit. Returns false when there
     * is no authentication context (safe default for redaction decisions).
     */
    public static boolean hasAnyRole(String... roles) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getAuthorities() == null) return false;
        for (var authority : auth.getAuthorities()) {
            String granted = authority.getAuthority();
            for (String role : roles) {
                if (granted.equals("ROLE_" + role) || granted.equals(role)) return true;
            }
        }
        return false;
    }
}
