package com.nelly.hivtbmonitoringsystem.config;

import com.nelly.hivtbmonitoringsystem.entity.Facility;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.FacilityRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final SystemUserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedDefaultFacility();
        seedAdminUser();
    }

    private void seedDefaultFacility() {
        if (facilityRepository.count() > 0) {
            return;
        }
        Facility facility = Facility.builder()
                .name("Dream Medical Center")
                .location("KG 123 St, Kigali")
                .district("Gasabo")
                .isActive(true)
                .build();
        facilityRepository.save(facility);
        log.info("Default facility created: Dream Medical Center");
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail("admin@hivtb.rw")) {
            return;
        }

        SystemUser admin = SystemUser.builder()
                .fullName("System Administrator")
                .email("admin@hivtb.rw")
                .phoneNumber("+250780000000")
                .passwordHash(passwordEncoder.encode("Admin@2026"))
                .role(UserRole.SYSTEM_ADMIN)
                .isActive(true)
                .preferredLanguage("en")
                .build();

        userRepository.save(admin);
        log.info("Default admin user created: admin@hivtb.rw / Admin@2026");
    }
}
