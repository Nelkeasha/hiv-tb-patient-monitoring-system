package com.nelly.hivtbmonitoringsystem.config;

import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.Facility;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
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
    private final ChwRepository chwRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedDefaultFacility();
        seedAdminUser();
        seedTestChw();
    }

    private void seedDefaultFacility() {
        if (facilityRepository.count() > 0) return;
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
        userRepository.findByEmail("admin@hivtb.rw").ifPresentOrElse(existing -> {
            boolean changed = false;
            if (!Boolean.TRUE.equals(existing.getIsActive())) {
                existing.setIsActive(true);
                changed = true;
                log.warn("Admin user was deactivated — re-activating: admin@hivtb.rw");
            }
            if (existing.getRole() != UserRole.SYSTEM_ADMIN) {
                existing.setRole(UserRole.SYSTEM_ADMIN);
                changed = true;
                log.warn("Admin role was changed — restoring SYSTEM_ADMIN: admin@hivtb.rw");
            }
            if (changed) userRepository.save(existing);
        }, () -> {
            SystemUser admin = SystemUser.builder()
                    .fullName("System Administrator")
                    .email("admin@hivtb.rw")
                    .phoneNumber("+250780000000")
                    .passwordHash(passwordEncoder.encode("Admin@2026"))
                    .role(UserRole.SYSTEM_ADMIN)
                    .isActive(true)
                    .mustChangePassword(false)
                    .preferredLanguage("en")
                    .build();
            userRepository.save(admin);
            log.info("Default admin user created: admin@hivtb.rw / Admin@2026");
        });
    }

    private void seedTestChw() {
        if (userRepository.existsByEmail("chw1@hivtb.rw")) return;
        Facility facility = facilityRepository.findAll().stream().findFirst().orElse(null);
        if (facility == null) return;

        SystemUser chwUser = SystemUser.builder()
                .fullName("Alice Uwimana")
                .email("chw1@hivtb.rw")
                .phoneNumber("+250781000001")
                .passwordHash(passwordEncoder.encode("Chw@2026"))
                .role(UserRole.CHW)
                .isActive(true)
                .mustChangePassword(false)
                .preferredLanguage("rw")
                .build();
        userRepository.save(chwUser);

        Chw chw = Chw.builder()
                .user(chwUser)
                .facility(facility)
                .assignedVillage("Kimisagara")
                .assignedSector("Nyarugenge")
                .employeeCode("CHW-001")
                .isActive(true)
                .build();
        chwRepository.save(chw);
        log.info("Test CHW created: chw1@hivtb.rw / Chw@2026");
    }
}
