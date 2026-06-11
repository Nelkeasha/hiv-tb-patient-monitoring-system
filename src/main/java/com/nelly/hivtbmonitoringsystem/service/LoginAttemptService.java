package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Records failed login attempts in its own transaction so the update survives
 * even though AuthService.login() rolls back after rethrowing the auth exception.
 */
@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_FAILED_ATTEMPTS = 3;

    private final SystemUserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailedAttempt(String email) {
        userRepository.findByEmail(email).ifPresent(u -> {
            int attempts = (u.getFailedLoginAttempts() == null ? 0 : u.getFailedLoginAttempts()) + 1;
            u.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                u.setAccountLocked(true);
            }
            userRepository.save(u);
        });
    }
}
