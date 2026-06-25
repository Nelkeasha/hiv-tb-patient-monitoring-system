package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.ConfirmPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.EnrollPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.RegisterPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.ScreenPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdatePatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.PatientResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.PendingAssignmentResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.ProvisionalPatientResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.FacilityProvider;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.FacilityProviderRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import com.nelly.hivtbmonitoringsystem.validation.UniquenessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final SystemUserRepository userRepository;
    private final ChwRepository chwRepository;
    private final FacilityProviderRepository facilityProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;
    private final NotificationService notificationService;
    private final UniquenessValidator uniquenessValidator;

    // ── Route B — CHW provisional screening ──────────────────────────────────

    @Transactional
    public ProvisionalPatientResponse screenPatient(ScreenPatientRequest req) {
        Chw chw = resolveCurrentChw();

        String patientCode = generatePatientCode();
        String referralId  = generateReferralId(req.getSector() != null ? req.getSector() : req.getVillage());

        DiagnosisType diagnosisType = parseSuspectedCondition(req.getSuspectedCondition());

        String symptoms = req.getSymptoms() != null
                ? String.join(",", req.getSymptoms())
                : null;

        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .fullName(req.getFullName())
                .dateOfBirth(req.getDateOfBirth())
                .sex(req.getSex() != null ? req.getSex() : "UNKNOWN")
                .phoneNumber(req.getPhoneNumber())
                .hasSmartphone(req.getHasSmartphone() != null ? req.getHasSmartphone() : false)
                .diagnosisType(diagnosisType)
                .province(req.getProvince())
                .district(req.getDistrict())
                .sector(req.getSector())
                .cell(req.getCell())
                .village(req.getVillage())
                .householdLocation(req.getHouseholdLocation())
                .locationGeohash(req.getLocationGeohash())
                .chw(chw)
                .facility(chw.getFacility())
                .registrationRoute("CHW_SCREENING")
                .registrationStatus("PROVISIONAL")
                .referralId(referralId)
                .screenedByChwId(chw.getId())
                .screenedAt(LocalDateTime.now())
                .suspectedCondition(req.getSuspectedCondition())
                .screeningSymptoms(symptoms)
                .screeningNotes(req.getScreeningNotes())
                // No syncStatus until clinical staff confirms — a PROVISIONAL
                // record has no verified diagnosis yet and must not appear as
                // "pending FHIR sync" alongside real confirmed patients.
                .syncStatus(null)
                .isActive(true)
                .consentGiven(req.isConsentGiven())
                .consentTimestamp(LocalDateTime.now())
                .consentVersion(req.getConsentVersion())
                .build();

        patientRepository.save(patient);
        auditLogService.log("SCREEN_PATIENT", "patients", patient.getId());

        String facilityName = chw.getFacility().getName();
        return ProvisionalPatientResponse.builder()
                .patientId(patient.getId())
                .patientCode(patientCode)
                .fullName(req.getFullName())
                .referralId(referralId)
                .status("PROVISIONAL")
                .message("Provisional screening record created. Referral ID: " + referralId +
                         ". Please give this ID to the patient to present at the health center.")
                .referralInstructions("Patient must present to " + facilityName +
                         " within 7 days for laboratory confirmation.")
                .build();
    }

    // ── Route A — Clinical staff facility registration ────────────────────────

    @Transactional
    public PatientResponse registerPatient(RegisterPatientRequest req) {
        SystemUser currentUser = resolveCurrentUser();

        Chw chw;
        if (req.getAssignedChwId() != null) {
            chw = chwRepository.findById(req.getAssignedChwId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "CHW not found"));
        } else {
            chw = matchChwByLocation(req.getVillage(), req.getSector())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "No CHW covers this village/sector — please select a CHW manually"));
        }

        uniquenessValidator.ensureUnique("nationalId",
                req.getNationalId() != null && !req.getNationalId().isBlank()
                        && patientRepository.existsByNationalId(req.getNationalId()),
                "A patient with this National ID is already registered");
        uniquenessValidator.ensureUnique("phoneNumber",
                req.getPhoneNumber() != null && !req.getPhoneNumber().isBlank()
                        && patientRepository.existsByPhoneNumber(req.getPhoneNumber()),
                "This phone number is already linked to another patient");
        uniquenessValidator.ensureUnique("phoneNumber",
                Boolean.TRUE.equals(req.getHasSmartphone()) && req.getPhoneNumber() != null
                        && userRepository.existsByPhoneNumber(req.getPhoneNumber()),
                "This phone number is already linked to another account");

        String patientCode = generatePatientCode();

        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .fullName(req.getFullName())
                .dateOfBirth(req.getDateOfBirth())
                .sex(req.getSex())
                .nationalId(req.getNationalId())
                .phoneNumber(req.getPhoneNumber())
                .hasSmartphone(req.getHasSmartphone() != null ? req.getHasSmartphone() : false)
                .diagnosisType(req.getDiagnosisType())
                .artStartDate(req.getArtStartDate())
                .tbTreatmentStartDate(req.getTbTreatmentStartDate())
                .province(req.getProvince())
                .district(req.getDistrict())
                .sector(req.getSector())
                .cell(req.getCell())
                .village(req.getVillage())
                .householdLocation(req.getHouseholdLocation())
                .locationGeohash(req.getLocationGeohash())
                .chw(chw)
                .facility(chw.getFacility())
                .registrationRoute("FACILITY")
                .registrationStatus("CONFIRMED")
                .confirmedBy(currentUser.getId())
                .confirmedAt(LocalDateTime.now())
                .syncStatus(SyncStatus.PENDING)
                .isActive(true)
                .chwAssignmentStatus("PENDING")
                .chwAssignedAt(LocalDateTime.now())
                .consentGiven(req.isConsentGiven())
                .consentTimestamp(LocalDateTime.now())
                .consentVersion(req.getConsentVersion())
                .build();

        patientRepository.save(patient);

        String loginEmail = null;
        String temporaryPassword = null;
        if (Boolean.TRUE.equals(req.getHasSmartphone())) {
            temporaryPassword = generateTempPassword();
            loginEmail = patientCode.toLowerCase() + "@hivtb.rw";
            SystemUser patientUser = SystemUser.builder()
                    .fullName(req.getFullName())
                    .email(loginEmail)
                    .phoneNumber(req.getPhoneNumber())
                    .passwordHash(passwordEncoder.encode(temporaryPassword))
                    .role(UserRole.PATIENT)
                    .isActive(true)
                    .mustChangePassword(true)
                    .preferredLanguage("rw")
                    .build();
            patientUser = userRepository.save(patientUser);
            patient.setUser(patientUser);
            patientRepository.save(patient);
        }

        auditLogService.log("REGISTER_PATIENT", "patients", patient.getId());

        // SMS credentials to patient if an account was just created
        if (loginEmail != null && patient.getPhoneNumber() != null) {
            notificationService.notifyPatientAccountCreated(
                    patient.getPhoneNumber(),
                    patient.getFullName(),
                    loginEmail,
                    temporaryPassword);
        }

        // Masked assignment notification — the CHW must accept before the full
        // record (name, diagnosis) is visible to them; see acceptAssignment().
        notificationService.notifyNewPatientAssignment(patient, chw);

        return toResponse(patient, loginEmail, temporaryPassword);
    }

    // ── Route B confirmation — clinical staff upgrades provisional to active ──

    @Transactional
    public PatientResponse confirmPatient(UUID patientId, ConfirmPatientRequest req) {
        SystemUser currentUser = resolveCurrentUser();

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        if (!"PROVISIONAL".equals(patient.getRegistrationStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Patient is already CONFIRMED — cannot confirm again");
        }

        patient.setRegistrationStatus("CONFIRMED");
        patient.setDiagnosisType(req.getDiagnosisType());
        if (req.getArtStartDate() != null)          patient.setArtStartDate(req.getArtStartDate());
        if (req.getTbTreatmentStartDate() != null)  patient.setTbTreatmentStartDate(req.getTbTreatmentStartDate());
        if (req.getNationalPatientId() != null)     patient.setNationalId(req.getNationalPatientId());
        if (req.getLabResultNotes() != null)        patient.setLabResultNotes(req.getLabResultNotes());
        patient.setConfirmedBy(currentUser.getId());
        patient.setConfirmedAt(LocalDateTime.now());
        patient.setSyncStatus(SyncStatus.PENDING);

        patientRepository.save(patient);

        // Create app account if patient has a smartphone and no account yet
        String createdLoginEmail = null;
        String createdTempPass   = null;
        if (Boolean.TRUE.equals(patient.getHasSmartphone()) && patient.getUser() == null) {
            createdTempPass  = generateTempPassword();
            createdLoginEmail = patient.getPatientCode().toLowerCase() + "@hivtb.rw";

            // system_users.phone_number is NOT NULL and UNIQUE. Reuse the patient's
            // phone if it's free; otherwise fall back to a synthetic value derived
            // from the patient code so the constraint is satisfied without colliding
            // with the CHW (or another user) who may already own that phone number.
            String userPhone = patient.getPhoneNumber();
            if (userPhone == null || userRepository.existsByPhoneNumber(userPhone)) {
                userPhone = "PT-" + patient.getPatientCode();
            }

            SystemUser patientUser = SystemUser.builder()
                    .fullName(patient.getFullName())
                    .email(createdLoginEmail)
                    .phoneNumber(userPhone)
                    .passwordHash(passwordEncoder.encode(createdTempPass))
                    .role(UserRole.PATIENT)
                    .isActive(true)
                    .mustChangePassword(true)
                    .preferredLanguage("rw")
                    .build();
            patientUser = userRepository.save(patientUser);
            patient.setUser(patientUser);
            patientRepository.save(patient);
        }
        auditLogService.log("CONFIRM_PATIENT", "patients", patient.getId());

        // SMS credentials to patient if account was just created
        if (createdLoginEmail != null && patient.getPhoneNumber() != null) {
            notificationService.notifyPatientAccountCreated(
                    patient.getPhoneNumber(),
                    patient.getFullName(),
                    createdLoginEmail,
                    createdTempPass);
        }

        // Notify the CHW who screened this patient
        notificationService.notifyPatientConfirmed(patient, patient.getChw(), currentUser.getFullName());

        return toResponse(patient, createdLoginEmail, createdTempPass);
    }

    // ── CHW: backward-compat enrollment (also creates PROVISIONAL) ────────────

    @Transactional
    public PatientResponse enrollPatient(EnrollPatientRequest req) {
        Chw chw = resolveCurrentChw();

        String patientCode = (req.getPatientCode() != null && !req.getPatientCode().isBlank())
                ? req.getPatientCode()
                : generatePatientCode();

        if (patientRepository.existsByPatientCode(patientCode)) {
            patientCode = generatePatientCode();
        }
        uniquenessValidator.ensureUnique("nationalId", "National ID", req.getNationalId(),
                req.getNationalId() != null && !req.getNationalId().isBlank()
                        && patientRepository.existsByNationalId(req.getNationalId()));

        DiagnosisType diagnosisType = req.getDiagnosisType();
        if (diagnosisType == null) {
            boolean hivPositive = "POSITIVE".equalsIgnoreCase(req.getHivStatus());
            boolean tbActive = req.getTbStatus() != null && !"NONE".equalsIgnoreCase(req.getTbStatus());
            if (hivPositive && tbActive) diagnosisType = DiagnosisType.HIV_TB_COINFECTION;
            else if (hivPositive)        diagnosisType = DiagnosisType.HIV;
            else                         diagnosisType = DiagnosisType.TB;
        }

        String referralId = generateReferralId(req.getSector() != null ? req.getSector() : req.getVillage());

        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .fullName(req.getFullName())
                .dateOfBirth(req.getDateOfBirth())
                .sex(req.getSex())
                .nationalId(req.getNationalId())
                .phoneNumber(req.getPhoneNumber())
                .hasSmartphone(req.getHasSmartphone() != null ? req.getHasSmartphone() : false)
                .diagnosisType(diagnosisType)
                .artStartDate(req.getArtStartDate())
                .tbTreatmentStartDate(req.getTbTreatmentStartDate())
                .householdLocation(req.getHouseholdLocation())
                .village(req.getVillage())
                .sector(req.getSector())
                .district(req.getDistrict())
                .chw(chw)
                .facility(chw.getFacility())
                .registrationRoute("CHW_SCREENING")
                .registrationStatus("PROVISIONAL")
                .referralId(referralId)
                .screenedByChwId(chw.getId())
                .screenedAt(LocalDateTime.now())
                .syncStatus(null)
                .isActive(true)
                // Lombok's @Builder ignores the field initializer unless
                // @Builder.Default is present, so this must be set explicitly
                // or consentGiven lands as NULL and violates the NOT NULL
                // constraint on patients.consent_given (V29). This route has no
                // consent-capture step of its own, so default to not-consented.
                .consentGiven(false)
                .build();

        patientRepository.save(patient);
        auditLogService.log("SCREEN_PATIENT", "patients", patient.getId());
        return toResponse(patient, null, null);
    }

    // ── Reads ─────────────────────────────────────────────────────────────────

    public List<PatientResponse> getMyPatients() {
        Chw chw = resolveCurrentChw();
        return patientRepository.findByChwId(chw.getId())
                .stream()
                .filter(p -> !"PENDING".equals(p.getChwAssignmentStatus()))
                .map(p -> toResponse(p, null, null)).collect(Collectors.toList());
    }

    public PatientResponse getPatient(UUID patientId) {
        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: patient not assigned to you");
        }
        if ("PENDING".equals(patient.getChwAssignmentStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Accept this patient assignment before viewing the full record");
        }
        return toResponse(patient, null, null);
    }

    /** Masked list — name/diagnosis withheld until the CHW accepts (see acceptAssignment). */
    public List<PendingAssignmentResponse> getPendingAssignments() {
        Chw chw = resolveCurrentChw();
        return patientRepository.findByChwId(chw.getId())
                .stream()
                .filter(p -> "PENDING".equals(p.getChwAssignmentStatus()))
                .map(p -> PendingAssignmentResponse.builder()
                        .patientId(p.getId())
                        .village(p.getVillage())
                        .sector(p.getSector())
                        .protocol(protocolLabel(p.getDiagnosisType()))
                        .assignedAt(p.getChwAssignedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public PatientResponse acceptAssignment(UUID patientId) {
        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
        }
        if (!"PENDING".equals(patient.getChwAssignmentStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Assignment already accepted");
        }

        patient.setChwAssignmentStatus("ACCEPTED");
        patient.setChwAcceptedAt(LocalDateTime.now());
        patientRepository.save(patient);
        auditLogService.log("ACCEPT_PATIENT_ASSIGNMENT", "patients", patient.getId());
        return toResponse(patient, null, null);
    }

    public List<PatientResponse> getAllActivePatients() {
        return patientRepository.findByIsActiveTrueAndRegistrationStatus("CONFIRMED")
                .stream().map(p -> toResponse(p, null, null)).collect(Collectors.toList());
    }

    /** Clinical/facility staff see only their own facility's provisional patients; admins see system-wide. */
    public List<PatientResponse> getProvisionalPatients() {
        SystemUser currentUser = resolveCurrentUser();
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.SYSTEM_ADMIN) {
            return patientRepository.findByRegistrationStatus("PROVISIONAL")
                    .stream().map(p -> toResponse(p, null, null)).collect(Collectors.toList());
        }
        FacilityProvider provider = facilityProviderRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Facility provider profile not found"));
        return patientRepository.findByFacilityIdAndRegistrationStatus(provider.getFacility().getId(), "PROVISIONAL")
                .stream().map(p -> toResponse(p, null, null)).collect(Collectors.toList());
    }

    /** Single patient — CHW must own the patient; clinical/supervisor/admin can read any. */
    public PatientResponse getPatientForAnyRole(UUID patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

        SystemUser currentUser = resolveCurrentUser();
        if (currentUser.getRole() == UserRole.CHW) {
            Chw chw = chwRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "CHW profile not found"));
            if (!patient.getChw().getId().equals(chw.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to you");
            }
        }

        return toResponse(patient, null, null);
    }

    /** All patients assigned to a specific CHW — for supervisors/clinical staff. */
    public List<PatientResponse> getPatientsForChw(UUID chwId) {
        return patientRepository.findByChwId(chwId)
                .stream().map(p -> toResponse(p, null, null)).collect(Collectors.toList());
    }

    @Transactional
    public PatientResponse updatePatient(UUID patientId, UpdatePatientRequest req) {
        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: patient not assigned to you");
        }

        if (req.getFullName() != null && !req.getFullName().isBlank())
                                                patient.setFullName(req.getFullName().trim());
        if (req.getPhoneNumber() != null)       patient.setPhoneNumber(req.getPhoneNumber());
        if (req.getHasSmartphone() != null)     patient.setHasSmartphone(req.getHasSmartphone());
        if (req.getHouseholdLocation() != null) patient.setHouseholdLocation(req.getHouseholdLocation());
        if (req.getVillage() != null)           patient.setVillage(req.getVillage());
        if (req.getSector() != null)            patient.setSector(req.getSector());
        if (req.getDistrict() != null)          patient.setDistrict(req.getDistrict());

        patientRepository.save(patient);
        return toResponse(patient, null, null);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private PatientResponse toResponse(Patient p, String loginEmail, String temporaryPassword) {
        return PatientResponse.builder()
                .id(p.getId())
                .patientCode(p.getPatientCode())
                .fullName(p.getFullName())
                .dateOfBirth(p.getDateOfBirth())
                .sex(p.getSex())
                .nationalId(p.getNationalId())
                .phoneNumber(p.getPhoneNumber())
                .hasSmartphone(p.getHasSmartphone())
                .diagnosisType(p.getDiagnosisType() != null ? p.getDiagnosisType().name() : null)
                .artStartDate(p.getArtStartDate())
                .tbTreatmentStartDate(p.getTbTreatmentStartDate())
                .householdLocation(p.getHouseholdLocation())
                .village(p.getVillage())
                .sector(p.getSector())
                .district(p.getDistrict())
                .province(p.getProvince())
                .cell(p.getCell())
                .locationGeohash(p.getLocationGeohash())
                .chwId(p.getChw().getId())
                .chwName(p.getChw().getUser().getFullName())
                .facilityId(p.getFacility().getId())
                .facilityName(p.getFacility().getName())
                .syncStatus(p.getSyncStatus() != null ? p.getSyncStatus().name() : null)
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .loginEmail(loginEmail)
                .temporaryPassword(temporaryPassword)
                .registrationRoute(p.getRegistrationRoute())
                .registrationStatus(p.getRegistrationStatus())
                .referralId(p.getReferralId())
                .suspectedCondition(p.getSuspectedCondition())
                .screeningNotes(p.getScreeningNotes())
                .labResultNotes(p.getLabResultNotes())
                .confirmedAt(p.getConfirmedAt())
                .build();
    }

    /** Village-level match takes priority; falls back to sector-level coverage. */
    /**
     * Matches the patient's village (falling back to sector) against active CHWs.
     * When more than one CHW covers the same village/sector, the one with the
     * fewest currently-active patients is picked, so a shared village's caseload
     * doesn't pile onto a single CHW.
     */
    private Optional<Chw> matchChwByLocation(String village, String sector) {
        List<Chw> candidates = List.of();
        if (village != null && !village.isBlank()) {
            candidates = chwRepository.findByIsActiveTrueAndAssignedVillageIgnoreCase(village.trim());
        }
        if (candidates.isEmpty() && sector != null && !sector.isBlank()) {
            candidates = chwRepository.findByIsActiveTrueAndAssignedSectorIgnoreCase(sector.trim());
        }
        if (candidates.isEmpty()) return Optional.empty();
        if (candidates.size() == 1) return Optional.of(candidates.get(0));

        return candidates.stream()
                .min(Comparator.comparingLong(c -> patientRepository.countByChwIdAndIsActiveTrue(c.getId())));
    }

    private String protocolLabel(DiagnosisType type) {
        return switch (type) {
            case TB -> "TB_DOT_ADHERENCE";
            case HIV -> "ART_ADHERENCE";
            case HIV_TB_COINFECTION -> "ART_TB_DOT_ADHERENCE";
        };
    }

    private Chw resolveCurrentChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("CHW profile not found for user: " + email));
    }

    private SystemUser resolveCurrentUser() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private String generatePatientCode() {
        return "PT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    private String generateReferralId(String location) {
        int year = java.time.LocalDate.now().getYear();
        String loc = (location != null && location.length() >= 3)
                ? location.substring(0, 3).toUpperCase().replaceAll("[^A-Z]", "X")
                : "GEN";
        String seq = String.format("%04d", new SecureRandom().nextInt(9000) + 1000);
        return "REF-" + year + "-" + loc + "-" + seq;
    }

    private DiagnosisType parseSuspectedCondition(String suspectedCondition) {
        if (suspectedCondition == null) return DiagnosisType.TB;
        return switch (suspectedCondition.toUpperCase().trim()) {
            case "HIV"                -> DiagnosisType.HIV;
            case "HIV_TB_COINFECTION",
                 "HIV_TB"             -> DiagnosisType.HIV_TB_COINFECTION;
            default                   -> DiagnosisType.TB;
        };
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("Pt@");
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }
}
