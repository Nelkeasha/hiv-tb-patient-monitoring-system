package com.nelly.hivtbmonitoringsystem.service;

import com.nelly.hivtbmonitoringsystem.dto.response.PatientResponse;
import com.nelly.hivtbmonitoringsystem.dto.response.TodayDoseResponse;
import com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog;
import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.entity.SystemUser;
import com.nelly.hivtbmonitoringsystem.entity.HomeVisit;
import com.nelly.hivtbmonitoringsystem.repository.ConfirmationLogRepository;
import com.nelly.hivtbmonitoringsystem.repository.DoseScheduleRepository;
import com.nelly.hivtbmonitoringsystem.repository.HomeVisitRepository;
import com.nelly.hivtbmonitoringsystem.repository.PatientRepository;
import com.nelly.hivtbmonitoringsystem.repository.SystemUserRepository;
import com.nelly.hivtbmonitoringsystem.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientSelfService {

    private final PatientRepository patientRepository;
    private final SystemUserRepository userRepository;
    private final DoseScheduleRepository doseScheduleRepository;
    private final ConfirmationLogRepository confirmationLogRepository;
    private final HomeVisitRepository homeVisitRepository;

    public PatientResponse getProfile() {
        Patient p = resolvePatient();

        // Next CHW visit date from the most recent home visit record
        LocalDate nextVisit = homeVisitRepository
                .findByPatientIdOrderByVisitDateDesc(p.getId())
                .stream()
                .filter(v -> v.getNextVisitDate() != null)
                .findFirst()
                .map(v -> v.getNextVisitDate().toLocalDate())
                .orElse(null);

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
                .chwId(p.getChw() != null ? p.getChw().getId() : null)
                .chwName(p.getChw() != null ? p.getChw().getUser().getFullName() : null)
                .facilityId(p.getFacility() != null ? p.getFacility().getId() : null)
                .facilityName(p.getFacility() != null ? p.getFacility().getName() : null)
                .syncStatus(p.getSyncStatus() != null ? p.getSyncStatus().name() : null)
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .nextCHWVisitDate(nextVisit)
                .build();
    }

    public List<TodayDoseResponse> getTodaySchedule() {
        Patient patient = resolvePatient();
        LocalDate today = LocalDate.now();
        List<DoseSchedule> schedules =
                doseScheduleRepository.findByPatientIdAndIsActiveTrue(patient.getId());

        return schedules.stream().map(s -> {
            // Same window as PatientConfirmationService / MissedDoseScheduler:
            // [doseTime, doseTime + windowDurationMinutes]. The app must never
            // show "missed" while the confirm endpoint would still accept.
            int window = s.getWindowDurationMinutes() != null ? s.getWindowDurationMinutes() : 45;
            LocalTime doseTime = s.getDoseTime();
            LocalDateTime windowOpen = today.atTime(doseTime);
            LocalDateTime windowClose = windowOpen.plusMinutes(window);

            Optional<ConfirmationLog> log =
                    confirmationLogRepository.findByScheduleIdAndScheduledDate(s.getId(), today);

            boolean isConfirmed = log.map(l -> l.getConfirmedAt() != null).orElse(false);
            boolean isMissed = log.map(l -> Boolean.TRUE.equals(l.getIsMissed()))
                    .orElse(!isConfirmed && LocalDateTime.now().isAfter(windowClose));
            LocalDateTime confirmedAt = log.map(ConfirmationLog::getConfirmedAt).orElse(null);

            if (log.isPresent()) {
                windowOpen = log.get().getWindowOpenTime();
                windowClose = log.get().getWindowCloseTime();
            }

            return TodayDoseResponse.builder()
                    .id(s.getId())
                    .patientId(patient.getId())
                    .medicationName(s.getDoseLabel() != null ? s.getDoseLabel() : "Medication")
                    .scheduledTime(doseTime.toString().substring(0, 5))
                    .windowOpenTime(windowOpen)
                    .windowCloseTime(windowClose)
                    .isConfirmed(isConfirmed)
                    .isMissed(isMissed)
                    .confirmedAt(confirmedAt)
                    .prescribedBy(s.getCreatedBy() != null ? s.getCreatedBy().getFullName() : null)
                    .facilityName(patient.getFacility() != null ? patient.getFacility().getName() : null)
                    .prescriptionSource(s.getPrescriptionSource())
                    .build();
        }).toList();
    }

    private Patient resolvePatient() {
        String email = SecurityUtil.getCurrentUserEmail();
        SystemUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        return patientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient profile not found"));
    }
}
