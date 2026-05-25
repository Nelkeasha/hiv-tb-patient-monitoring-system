package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.ConfirmationLog;
import com.nelly.hivtbmonitoringsystem.enums.ConfirmationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConfirmationLogRepository extends JpaRepository<ConfirmationLog, UUID> {
    List<ConfirmationLog> findByPatientId(UUID patientId);
    List<ConfirmationLog> findByPatientIdAndStatus(UUID patientId, ConfirmationStatus status);
    List<ConfirmationLog> findByPatientIdAndScheduledDoseTimeBetween(UUID patientId, LocalDateTime from, LocalDateTime to);
    long countByPatientIdAndStatusAndScheduledDoseTimeAfter(UUID patientId, ConfirmationStatus status, LocalDateTime after);
}
