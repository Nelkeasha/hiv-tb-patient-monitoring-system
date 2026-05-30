package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.request.EnrollPatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.request.UpdatePatientRequest;
import com.nelly.hivtbmonitoringsystem.dto.response.PatientResponse;
import com.nelly.hivtbmonitoringsystem.entity.Chw;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import com.nelly.hivtbmonitoringsystem.repository.ChwRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import com.nelly.hivtbmonitoringsystem.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final SystemUserRepository userRepository;
    private final ChwRepository chwRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public PatientResponse enrollPatient(EnrollPatientRequest req) {
        Chw chw = resolveCurrentChw();

        // Auto-generate patient code if not provided
        String patientCode = (req.getPatientCode() != null && !req.getPatientCode().isBlank())
                ? req.getPatientCode()
                : "PT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        if (patientRepository.existsByPatientCode(patientCode)) {
            patientCode = "PT-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }
        if (req.getNationalId() != null && patientRepository.existsByNationalId(req.getNationalId())) {
            throw new RuntimeException("National ID already registered: " + req.getNationalId());
        }

        // Accept "gender" as alias for "sex"
        String sex = (req.getSex() != null && !req.getSex().isBlank()) ? req.getSex() : req.getGender();

        // Derive diagnosisType from hivStatus + tbStatus if not provided directly
        DiagnosisType diagnosisType = req.getDiagnosisType();
        if (diagnosisType == null) {
            boolean hivPositive = "POSITIVE".equalsIgnoreCase(req.getHivStatus());
            boolean tbActive = req.getTbStatus() != null && !"NONE".equalsIgnoreCase(req.getTbStatus());
            if (hivPositive && tbActive) diagnosisType = DiagnosisType.HIV_TB_COINFECTION;
            else if (hivPositive) diagnosisType = DiagnosisType.HIV;
            else diagnosisType = DiagnosisType.TB;
        }

        Patient patient = Patient.builder()
                .patientCode(patientCode)
                .fullName(req.getFullName())
                .dateOfBirth(req.getDateOfBirth())
                .sex(sex)
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
                .syncStatus(SyncStatus.PENDING)
                .isActive(true)
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

        return toResponse(patient, loginEmail, temporaryPassword);
    }

    public List<PatientResponse> getMyPatients() {
        Chw chw = resolveCurrentChw();
        return patientRepository.findByChwIdAndIsActiveTrue(chw.getId())
                .stream().map(p -> toResponse(p, null, null)).collect(Collectors.toList());
    }

    public PatientResponse getPatient(UUID patientId) {
        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: patient not assigned to you");
        }
        return toResponse(patient, null, null);
    }

    public List<PatientResponse> getAllActivePatients() {
        return patientRepository.findAllByIsActiveTrue()
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

        if (req.getFullName() != null) patient.setFullName(req.getFullName());
        if (req.getPhoneNumber() != null) patient.setPhoneNumber(req.getPhoneNumber());
        if (req.getHasSmartphone() != null) patient.setHasSmartphone(req.getHasSmartphone());
        if (req.getHouseholdLocation() != null) patient.setHouseholdLocation(req.getHouseholdLocation());
        if (req.getVillage() != null) patient.setVillage(req.getVillage());
        if (req.getSector() != null) patient.setSector(req.getSector());
        if (req.getDistrict() != null) patient.setDistrict(req.getDistrict());

        patientRepository.save(patient);
        return toResponse(patient, null, null);
    }

    private Chw resolveCurrentChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("CHW profile not found for user: " + email));
    }

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
                .diagnosisType(p.getDiagnosisType().name())
                .artStartDate(p.getArtStartDate())
                .tbTreatmentStartDate(p.getTbTreatmentStartDate())
                .householdLocation(p.getHouseholdLocation())
                .village(p.getVillage())
                .sector(p.getSector())
                .district(p.getDistrict())
                .chwId(p.getChw().getId())
                .chwName(p.getChw().getUser().getFullName())
                .facilityId(p.getFacility().getId())
                .facilityName(p.getFacility().getName())
                .syncStatus(p.getSyncStatus().name())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .loginEmail(loginEmail)
                .temporaryPassword(temporaryPassword)
                .build();
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz23456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder("Pt@");
        for (int i = 0; i < 6; i++) sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }
}
