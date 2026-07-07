package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.RegisterPatientRequest;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.Facility;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.FacilityProviderRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.validation.UniquenessValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Verifies the village-based CHW auto-assignment tie-break added to
 * PatientService#matchChwByLocation: when two CHWs cover the same village,
 * the patient must go to whichever CHW currently has fewer active patients.
 */
class PatientServiceTest {

    private final PatientRepository patientRepository = mock(PatientRepository.class);
    private final SystemUserRepository userRepository = mock(SystemUserRepository.class);
    private final ChwRepository chwRepository = mock(ChwRepository.class);
    private final FacilityProviderRepository facilityProviderRepository = mock(FacilityProviderRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final AuditLogService auditLogService = mock(AuditLogService.class);
    private final NotificationService notificationService = mock(NotificationService.class);
    private final HomeVisitTaskService homeVisitTaskService = mock(HomeVisitTaskService.class);
    private final AlertService alertService = mock(AlertService.class);
    private final UniquenessValidator uniquenessValidator = new UniquenessValidator();

    private final PatientService patientService = new PatientService(
            patientRepository, userRepository, chwRepository,
            facilityProviderRepository, passwordEncoder, auditLogService, notificationService,
            homeVisitTaskService, alertService, uniquenessValidator);

    @BeforeEach
    void setUpSecurityContext() {
        SystemUser clinicalStaff = SystemUser.builder()
                .id(UUID.randomUUID())
                .fullName("Dr. Uwase")
                .email("provider@dreammedical.rw")
                .role(UserRole.FACILITY_PROVIDER)
                .build();
        when(userRepository.findByEmail("provider@dreammedical.rw")).thenReturn(Optional.of(clinicalStaff));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("provider@dreammedical.rw", null));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void registerPatient_assignsToChwWithFewerActivePatients_whenTwoChwsShareVillage() {
        Facility facility = Facility.builder().id(UUID.randomUUID()).name("Dream Medical Center").build();

        Chw busyChw = Chw.builder()
                .id(UUID.randomUUID())
                .user(SystemUser.builder().id(UUID.randomUUID()).fullName("CHW Busy").build())
                .facility(facility)
                .assignedVillage("Kacyiru")
                .assignedSector("Kacyiru")
                .employeeCode("CHW-001")
                .isActive(true)
                .build();

        Chw lightlyLoadedChw = Chw.builder()
                .id(UUID.randomUUID())
                .user(SystemUser.builder().id(UUID.randomUUID()).fullName("CHW Light").build())
                .facility(facility)
                .assignedVillage("Kacyiru")
                .assignedSector("Kacyiru")
                .employeeCode("CHW-002")
                .isActive(true)
                .build();

        when(chwRepository.findByIsActiveTrueAndAssignedVillageIgnoreCase("Kacyiru"))
                .thenReturn(List.of(busyChw, lightlyLoadedChw));
        when(patientRepository.countByChwIdAndIsActiveTrue(busyChw.getId())).thenReturn(12L);
        when(patientRepository.countByChwIdAndIsActiveTrue(lightlyLoadedChw.getId())).thenReturn(2L);

        when(patientRepository.existsByNationalId(any())).thenReturn(false);
        when(patientRepository.existsByPhoneNumber(any())).thenReturn(false);

        RegisterPatientRequest req = new RegisterPatientRequest();
        req.setFullName("Jean Mugisha");
        req.setDateOfBirth(LocalDate.of(1990, 5, 1));
        req.setSex("MALE");
        req.setDiagnosisType(DiagnosisType.HIV);
        req.setVillage("Kacyiru");
        req.setSector("Kacyiru");
        req.setHasSmartphone(false);
        req.setConsentGiven(true);
        req.setConsentVersion("v1");
        // assignedChwId intentionally left null so the village/caseload tie-break runs.

        patientService.registerPatient(req);

        var patientCaptor = org.mockito.ArgumentCaptor.forClass(Patient.class);
        org.mockito.Mockito.verify(patientRepository, org.mockito.Mockito.atLeastOnce()).save(patientCaptor.capture());
        Patient savedPatient = patientCaptor.getValue();

        assertThat(savedPatient.getChw().getId()).isEqualTo(lightlyLoadedChw.getId());
    }

    @Test
    void registerPatient_assignsTheOnlyMatchingChw_whenOnlyOneChwCoversTheVillage() {
        Facility facility = Facility.builder().id(UUID.randomUUID()).name("Dream Medical Center").build();

        Chw onlyChw = Chw.builder()
                .id(UUID.randomUUID())
                .user(SystemUser.builder().id(UUID.randomUUID()).fullName("CHW Solo").build())
                .facility(facility)
                .assignedVillage("Remera")
                .assignedSector("Remera")
                .employeeCode("CHW-010")
                .isActive(true)
                .build();

        when(chwRepository.findByIsActiveTrueAndAssignedVillageIgnoreCase("Remera"))
                .thenReturn(List.of(onlyChw));

        when(patientRepository.existsByNationalId(any())).thenReturn(false);
        when(patientRepository.existsByPhoneNumber(any())).thenReturn(false);

        RegisterPatientRequest req = new RegisterPatientRequest();
        req.setFullName("Alice Niyonsaba");
        req.setDateOfBirth(LocalDate.of(1985, 3, 12));
        req.setSex("FEMALE");
        req.setDiagnosisType(DiagnosisType.TB);
        req.setVillage("Remera");
        req.setHasSmartphone(false);
        req.setConsentGiven(true);
        req.setConsentVersion("v1");

        patientService.registerPatient(req);

        var patientCaptor = org.mockito.ArgumentCaptor.forClass(Patient.class);
        org.mockito.Mockito.verify(patientRepository, org.mockito.Mockito.atLeastOnce()).save(patientCaptor.capture());
        Patient savedPatient = patientCaptor.getValue();

        assertThat(savedPatient.getChw().getId()).isEqualTo(onlyChw.getId());
        // single-candidate path must not need a caseload lookup at all
        org.mockito.Mockito.verify(patientRepository, org.mockito.Mockito.never())
                .countByChwIdAndIsActiveTrue(eq(onlyChw.getId()));
    }
}
