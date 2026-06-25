package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Patient;
import com.nelly.hivtbmonitoringsystem.enums.DiagnosisType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByPatientCode(String patientCode);
    Optional<Patient> findByNationalId(String nationalId);
    Optional<Patient> findByPhoneNumber(String phoneNumber);
    Optional<Patient> findByFhirPatientId(String fhirPatientId);
    List<Patient> findByChwId(UUID chwId);
    List<Patient> findByFacilityId(UUID facilityId);
    List<Patient> findByChwIdAndIsActiveTrue(UUID chwId);
    List<Patient> findByDiagnosisType(DiagnosisType diagnosisType);
    boolean existsByPatientCode(String patientCode);
    boolean existsByNationalId(String nationalId);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Patient> findByUserId(UUID userId);
    List<Patient> findByFacilityIdAndIsActiveTrue(UUID facilityId);
    /** "Active patient" for reports/dashboards means confirmed AND active — excludes PROVISIONAL screenings still awaiting confirmation. */
    List<Patient> findByFacilityIdAndIsActiveTrueAndRegistrationStatus(UUID facilityId, String registrationStatus);
    /**
     * PROVISIONAL patients never get syncStatus=PENDING set in the first place
     * (it's left null until clinical confirmation), so these three already
     * exclude them today — the explicit registrationStatus filter here is
     * defense in depth, so a future caller that relaxes the syncStatus match
     * (e.g. "!= SYNCED" instead of "== PENDING") can't silently re-include
     * unconfirmed patients in FHIR sync-pending counts.
     */
    List<Patient> findByChwIdAndSyncStatusAndRegistrationStatus(
            UUID chwId, com.nelly.hivtbmonitoringsystem.enums.SyncStatus syncStatus, String registrationStatus);
    long countByFacilityIdAndSyncStatusAndRegistrationStatus(
            UUID facilityId, com.nelly.hivtbmonitoringsystem.enums.SyncStatus syncStatus, String registrationStatus);
    long countBySyncStatusAndRegistrationStatus(
            com.nelly.hivtbmonitoringsystem.enums.SyncStatus syncStatus, String registrationStatus);
    List<Patient> findAllByIsActiveTrue();
    List<Patient> findByIsActiveTrueAndRegistrationStatus(String registrationStatus);
    List<Patient> findByRegistrationStatus(String registrationStatus);
    List<Patient> findByFacilityIdAndRegistrationStatus(UUID facilityId, String registrationStatus);
    List<Patient> findByChwAssignmentStatus(String chwAssignmentStatus);
    /** Current caseload used to tie-break CHW auto-assignment when several CHWs cover the same village/sector. */
    long countByChwIdAndIsActiveTrue(UUID chwId);
}
