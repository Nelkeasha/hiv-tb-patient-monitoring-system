package com.nelly.hivtbmonitoringsystem.controller;

import com.nelly.hivtbmonitoringsystem.dto.response.PatientResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.TodayDoseResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.TreatmentPlanResponse;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.service.PatientSelfService;
import com.nelly.hivtbmonitoringsystem.service.TreatmentPlanService;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/patient")
@RequiredArgsConstructor
@PreAuthorize("hasRole('PATIENT')")
public class PatientSelfController {

    private final PatientSelfService patientSelfService;
    private final TreatmentPlanService treatmentPlanService;
    private final SystemUserRepository userRepository;
    private final PatientRepository patientRepository;

    @GetMapping("/profile")
    public ResponseEntity<PatientResponse> getProfile() {
        return ResponseEntity.ok(patientSelfService.getProfile());
    }

    @GetMapping("/schedule")
    public ResponseEntity<List<TodayDoseResponse>> getTodaySchedule() {
        return ResponseEntity.ok(patientSelfService.getTodaySchedule());
    }

    @GetMapping("/treatment-plans")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<TreatmentPlanResponse>> getMyTreatmentPlans() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        Patient patient = patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient profile not found"));
        return ResponseEntity.ok(treatmentPlanService.getOwnPatientPlans(patient.getId()));
    }
}
