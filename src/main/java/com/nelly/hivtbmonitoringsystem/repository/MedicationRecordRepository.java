package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Long> {
    List<MedicationRecord> findByPatientId(UUID patientId);
    List<MedicationRecord> findByPatientIdOrderByPeriodStartDesc(UUID patientId);
    Optional<MedicationRecord> findByPatientIdAndMedicationNameAndPeriodStart(UUID patientId, String medicationName, LocalDate periodStart);
    List<MedicationRecord> findByBelowThresholdTrue();
}
