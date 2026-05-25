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

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;
    private final SystemUserRepository userRepository;
    private final ChwRepository chwRepository;

    @Transactional
    public PatientResponse enrollPatient(EnrollPatientRequest req) {
        Chw chw = resolveCurrentChw();

        if (patientRepository.existsByPatientCode(req.getPatientCode())) {
            throw new RuntimeException("Patient code already exists: " + req.getPatientCode());
        }
        if (req.getNationalId() != null && patientRepository.existsByNationalId(req.getNationalId())) {
            throw new RuntimeException("National ID already registered: " + req.getNationalId());
        }

        Patient patient = Patient.builder()
                .patientCode(req.getPatientCode())
                .fullName(req.getFullName())
                .dateOfBirth(req.getDateOfBirth())
                .sex(req.getSex())
                .nationalId(req.getNationalId())
                .phoneNumber(req.getPhoneNumber())
                .hasSmartphone(req.getHasSmartphone() != null ? req.getHasSmartphone() : false)
                .diagnosisType(req.getDiagnosisType())
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
        return toResponse(patient);
    }

    public List<PatientResponse> getMyPatients() {
        Chw chw = resolveCurrentChw();
        return patientRepository.findByChwIdAndIsActiveTrue(chw.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public PatientResponse getPatient(UUID patientId) {
        Chw chw = resolveCurrentChw();
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found: " + patientId));
        if (!patient.getChw().getId().equals(chw.getId())) {
            throw new RuntimeException("Access denied: patient not assigned to you");
        }
        return toResponse(patient);
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
        return toResponse(patient);
    }

    private Chw resolveCurrentChw() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        return chwRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("CHW profile not found for user: " + email));
    }

    private PatientResponse toResponse(Patient p) {
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
                .build();
    }
}
