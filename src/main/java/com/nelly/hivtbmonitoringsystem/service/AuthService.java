package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.AcceptConsentRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ChangePasswordRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.LoginRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RefreshTokenRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.AuthResponse;

import com.nelly.hivtbmonitoringsystem.entity.RefreshToken;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.repository.RefreshTokenRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authManager;
    private final SystemUserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final LoginAttemptService loginAttemptService;

    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (AuthenticationException ex) {
            UUID failedUserId = userRepository.findByEmail(request.getEmail())
                    .map(SystemUser::getId)
                    .orElse(null);

            if (ex instanceof BadCredentialsException) {
                loginAttemptService.recordFailedAttempt(request.getEmail());
            }

            auditLogService.log(ex instanceof LockedException ? "LOGIN_BLOCKED_LOCKED" : "LOGIN_FAILED",
                    "system_users", failedUserId,
                    "{\"email\":\"" + request.getEmail() + "\"}", null);
            throw ex;
        }

        SystemUser user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails, user.getRole().name());
        String refreshTokenValue = generateRefreshToken(user);

        auditLogService.log("LOGIN", "system_users", user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .mustChangePassword(Boolean.TRUE.equals(user.getMustChangePassword()))
                .consentGiven(Boolean.TRUE.equals(user.getConsentGiven()))
                .build();
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
        auditLogService.log("CHANGE_PASSWORD", "system_users", user.getId());
    }

    /** Records that the user agreed to data-collection consent — required before the app unlocks past login. */
    @Transactional
    public void acceptConsent(String email, AcceptConsentRequest request) {
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setConsentGiven(true);
        user.setConsentTimestamp(LocalDateTime.now());
        user.setConsentVersion(request.getConsentVersion());
        userRepository.save(user);
        auditLogService.log("ACCEPT_CONSENT", "system_users", user.getId());
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken stored = refreshTokenRepository.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (stored.getIsRevoked() || stored.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Refresh token expired or revoked");
        }

        SystemUser user = stored.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails, user.getRole().name());

        stored.setIsRevoked(true);
        refreshTokenRepository.save(stored);
        String newRefreshToken = generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.findByToken(refreshTokenValue)
                .ifPresent(t -> {
                    t.setIsRevoked(true);
                    refreshTokenRepository.save(t);
                });
    }

    private String generateRefreshToken(SystemUser user) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .isRevoked(false)
                .build();
        return refreshTokenRepository.save(token).getToken();
    }
}
