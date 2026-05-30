package com.nelly.hivtbmonitoringsystem.config;

import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.Facility;
import com.nelly.hivtbmonitoringsystem.entity.StockRecord;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.FacilityRepository;
import com.nelly.hivtbmonitoringsystem.repository.StockRecordRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final SystemUserRepository userRepository;
    private final FacilityRepository facilityRepository;
    private final ChwRepository chwRepository;
    private final StockRecordRepository stockRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedDefaultFacility();
        seedAdminUser();
        seedTestChw();
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

    private void seedTestChw() {
        if (userRepository.existsByEmail("chw1@hivtb.rw")) {
            return;
        }
        Facility facility = facilityRepository.findAll().stream().findFirst()
                .orElse(null);
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

        seedTestStock(chw);
    }

    private void seedTestStock(Chw chw) {
        if (stockRepository.findByChwId(chw.getId()).size() > 0) {
            return;
        }

        List<StockRecord> stocks = List.of(
            StockRecord.builder()
                .chw(chw).medicationName("TDF/3TC/DTG (ART)").currentQuantity(120)
                .reorderLevel(30).unit("tablets").daysRemaining(60)
                .resupplyRequested(false).lastRestockedAt(LocalDateTime.now()).build(),
            StockRecord.builder()
                .chw(chw).medicationName("Rifampicin + Isoniazid (TB)").currentQuantity(90)
                .reorderLevel(30).unit("tablets").daysRemaining(45)
                .resupplyRequested(false).lastRestockedAt(LocalDateTime.now()).build(),
            StockRecord.builder()
                .chw(chw).medicationName("Pyrazinamide (TB)").currentQuantity(20)
                .reorderLevel(30).unit("tablets").daysRemaining(10)
                .resupplyRequested(true).lastRestockedAt(LocalDateTime.now().minusDays(14)).build(),
            StockRecord.builder()
                .chw(chw).medicationName("Cotrimoxazole (Prophylaxis)").currentQuantity(8)
                .reorderLevel(30).unit("tablets").daysRemaining(4)
                .resupplyRequested(false).lastRestockedAt(LocalDateTime.now().minusDays(30)).build()
        );

        stockRepository.saveAll(stocks);
        log.info("Test stock seeded for CHW: {}", chw.getEmployeeCode());
    }
}
