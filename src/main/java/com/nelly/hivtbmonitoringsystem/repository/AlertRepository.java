package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Alert;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByPatientId(UUID patientId);
    List<Alert> findByChwId(UUID chwId);
    List<Alert> findByProviderId(UUID providerId);
    List<Alert> findBySupervisorId(UUID supervisorId);
    List<Alert> findByIsResolvedFalse();
    List<Alert> findByPatientIdAndIsResolvedFalse(UUID patientId);
    List<Alert> findByAlertTypeAndIsResolvedFalse(AlertType alertType);
    List<Alert> findBySeverityAndIsResolvedFalse(AlertSeverity severity);
}
