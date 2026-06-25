package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.request.AcceptConsentRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ChangePasswordRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.LoginRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RefreshTokenRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.AuthResponse;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SystemUserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/consent")
    public ResponseEntity<Void> acceptConsent(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AcceptConsentRequest request) {
        authService.acceptConsent(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Mobile app registers its FCM device token after login.
     * Called once per login session. Token is stored per user and used
     * for push notifications (LTFU alerts, missed dose, false confirmation).
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<Void> registerFcmToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody FcmTokenRequest request) {
        userRepository.findByEmail(userDetails.getUsername()).ifPresent(user -> {
            user.setFcmToken(request.getToken());
            userRepository.save(user);
        });
        return ResponseEntity.noContent().build();
    }

    @Getter
    static class FcmTokenRequest {
        @NotBlank
        private String token;
    }
}
