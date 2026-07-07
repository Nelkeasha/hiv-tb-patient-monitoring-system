package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Alert;
import com.nelly.hivtbmonitoringsystem.enums.AlertSeverity;
import com.nelly.hivtbmonitoringsystem.enums.AlertType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    List<Alert> findByPatientId(UUID patientId);
    List<Alert> findByChwId(UUID chwId);
    List<Alert> findByProviderId(UUID providerId);
    List<Alert> findBySupervisorId(UUID supervisorId);
    List<Alert> findByIsResolvedFalse();
    List<Alert> findByPatientIdAndIsResolvedFalse(UUID patientId);
    List<Alert> findByPatientIdAndAlertTypeAndIsResolvedFalse(UUID patientId, AlertType alertType);
    Optional<Alert> findFirstByPatientIdAndAlertTypeAndIsResolvedFalseOrderByCreatedAtDesc(UUID patientId, AlertType alertType);
    List<Alert> findByAlertTypeAndIsResolvedFalse(AlertType alertType);
    // Resolved history for the clinical alerts "Resolved" view (newest resolution first).
    List<Alert> findBySeverityInAndIsResolvedTrueOrderByResolvedAtDesc(List<AlertSeverity> severities);
    List<Alert> findBySeverityAndIsResolvedFalse(AlertSeverity severity);
    List<Alert> findByChwIdAndIsResolvedFalseOrderByCreatedAtDesc(UUID chwId);
    List<Alert> findByChwIdAndIsReadFalseOrderByCreatedAtDesc(UUID chwId);
    List<Alert> findByProviderIdAndIsResolvedFalseOrderByCreatedAtDesc(UUID providerId);
    List<Alert> findBySeverityInAndIsResolvedFalseOrderByCreatedAtDesc(List<AlertSeverity> severities);
    List<Alert> findByPatientIdAndIsResolvedFalseOrderByCreatedAtDesc(UUID patientId);

    @org.springframework.data.jpa.repository.Query(
            "SELECT a FROM Alert a WHERE a.chw.facility.id = :facilityId " +
            "AND a.isResolved = false ORDER BY a.createdAt DESC")
    List<Alert> findUnresolvedAlertsForFacilityChws(
            @org.springframework.data.repository.query.Param("facilityId") UUID facilityId);

    @org.springframework.data.jpa.repository.Query(
            "SELECT a FROM Alert a WHERE a.patient.facility.id = :facilityId ORDER BY a.createdAt DESC")
    List<Alert> findByPatientFacilityId(
            @org.springframework.data.repository.query.Param("facilityId") UUID facilityId);

    @org.springframework.data.jpa.repository.Query(
            "SELECT a FROM Alert a WHERE a.isResolved = false AND a.escalatedAt IS NULL " +
            "AND a.createdAt < :cutoff")
    List<Alert> findUnacknowledgedAlertsOlderThan(
            @org.springframework.data.repository.query.Param("cutoff") java.time.LocalDateTime cutoff);
}
