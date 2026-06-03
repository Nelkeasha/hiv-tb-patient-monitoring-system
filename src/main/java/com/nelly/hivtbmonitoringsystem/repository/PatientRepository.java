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
    Optional<Patient> findByFhirPatientId(String fhirPatientId);
    List<Patient> findByChwId(UUID chwId);
    List<Patient> findByFacilityId(UUID facilityId);
    List<Patient> findByChwIdAndIsActiveTrue(UUID chwId);
    List<Patient> findByDiagnosisType(DiagnosisType diagnosisType);
    boolean existsByPatientCode(String patientCode);
    boolean existsByNationalId(String nationalId);
    Optional<Patient> findByUserId(UUID userId);
    List<Patient> findByFacilityIdAndIsActiveTrue(UUID facilityId);
    List<Patient> findByChwIdAndSyncStatus(UUID chwId, com.nelly.hivtbmonitoringsystem.enums.SyncStatus syncStatus);
    long countByFacilityIdAndSyncStatus(UUID facilityId, com.nelly.hivtbmonitoringsystem.enums.SyncStatus syncStatus);
    List<Patient> findAllByIsActiveTrue();
    List<Patient> findByRegistrationStatus(String registrationStatus);
}
