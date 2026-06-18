package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConfirmationLogRepository extends JpaRepository<ConfirmationLog, UUID> {
    List<ConfirmationLog> findByPatientId(UUID patientId);
    List<ConfirmationLog> findByPatientIdAndScheduledDate(UUID patientId, LocalDate date);
    List<ConfirmationLog> findByPatientIdAndScheduledDateBetween(UUID patientId, LocalDate from, LocalDate to);
    List<ConfirmationLog> findByPatientIdAndIsMissedTrue(UUID patientId);
    List<ConfirmationLog> findByPatientIdAndAiSuspicionFlagTrue(UUID patientId);
    long countByPatientIdAndIsMissedTrueAndScheduledDateAfter(UUID patientId, LocalDate after);
    java.util.Optional<ConfirmationLog> findByScheduleIdAndScheduledDate(UUID scheduleId, LocalDate date);
    List<ConfirmationLog> findByIsMissedTrueAndScheduledDateBetween(LocalDate from, LocalDate to);

    @org.springframework.data.jpa.repository.Query(
            "SELECT COUNT(cl) FROM ConfirmationLog cl " +
            "WHERE cl.patient.chw.id = :chwId AND cl.isMissed = true AND cl.scheduledDate > :after")
    long countMissedDosesByChwSince(
            @org.springframework.data.repository.query.Param("chwId") UUID chwId,
            @org.springframework.data.repository.query.Param("after") LocalDate after);

    /** All logs for a facility on a specific date — used for daily adherence trend. */
    @Query("SELECT cl FROM ConfirmationLog cl " +
           "WHERE cl.patient.facility.id = :facilityId AND cl.scheduledDate = :date")
    List<ConfirmationLog> findByFacilityIdAndScheduledDate(
            @Param("facilityId") UUID facilityId,
            @Param("date") LocalDate date);

    /** Missed logs for a facility on a specific date — used for supervisor missed trend. */
    @Query("SELECT COUNT(cl) FROM ConfirmationLog cl " +
           "WHERE cl.patient.facility.id = :facilityId AND cl.scheduledDate = :date AND cl.isMissed = true")
    long countMissedByFacilityAndDate(
            @Param("facilityId") UUID facilityId,
            @Param("date") LocalDate date);

    /**
     * Every distinct (patient, plan, day) combination that has at least one
     * confirmation/missed-dose log — used to backfill medication_records for
     * history that predates MedicationRecordService being wired in.
     */
    @Query("SELECT DISTINCT cl.patient.id, cl.plan.id, cl.scheduledDate FROM ConfirmationLog cl " +
           "WHERE cl.plan IS NOT NULL")
    List<Object[]> findDistinctPatientPlanDateCombos();
}
