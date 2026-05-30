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
}
