package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
