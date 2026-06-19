package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.MedicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicationRecordRepository extends JpaRepository<MedicationRecord, Long> {
    List<MedicationRecord> findByPatientId(UUID patientId);
    List<MedicationRecord> findByPatientIdOrderByPeriodStartDesc(UUID patientId);
    Optional<MedicationRecord> findByPatientIdAndPlanIdAndPeriodStart(UUID patientId, UUID planId, LocalDate periodStart);
    List<MedicationRecord> findByBelowThresholdTrue();
    List<MedicationRecord> findByFalseConfirmationFlagTrue();
    List<MedicationRecord> findByPlanId(UUID planId);

    @Query("SELECT mr FROM MedicationRecord mr WHERE mr.patient.facility.id = :facilityId")
    List<MedicationRecord> findByFacilityId(@Param("facilityId") UUID facilityId);

    @Query("SELECT mr FROM MedicationRecord mr WHERE mr.patient.facility.id = :facilityId AND mr.belowThreshold = true")
    List<MedicationRecord> findBelowThresholdByFacilityId(@Param("facilityId") UUID facilityId);

    @Query("SELECT mr FROM MedicationRecord mr WHERE mr.patient.chw.id = :chwId AND mr.syncStatus = :status")
    List<MedicationRecord> findByChwIdAndSyncStatus(@Param("chwId") UUID chwId,
                                                     @Param("status") com.nelly.hivtbmonitoringsystem.enums.SyncStatus status);

    @Query("SELECT COUNT(mr) FROM MedicationRecord mr WHERE mr.patient.facility.id = :facilityId AND mr.syncStatus = :status")
    long countByFacilityIdAndSyncStatus(@Param("facilityId") UUID facilityId,
                                         @Param("status") com.nelly.hivtbmonitoringsystem.enums.SyncStatus status);

    long countBySyncStatus(com.nelly.hivtbmonitoringsystem.enums.SyncStatus status);
}
