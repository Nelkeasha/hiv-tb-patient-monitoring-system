package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, UUID> {
    List<TreatmentPlan> findByPatientId(UUID patientId);
    Optional<TreatmentPlan> findByPatientIdAndIsActiveTrue(UUID patientId);
    Optional<TreatmentPlan> findByFhirCarePlanId(String fhirCarePlanId);

    @Query("SELECT COUNT(tp) FROM TreatmentPlan tp WHERE tp.patient.facility.id = :facilityId AND tp.isActive = true")
    long countActivePlansForFacility(@Param("facilityId") UUID facilityId);

    @Query("SELECT tp FROM TreatmentPlan tp WHERE tp.patient.chw.id = :chwId AND tp.syncStatus = :status")
    List<TreatmentPlan> findByChwIdAndSyncStatus(@Param("chwId") UUID chwId,
                                                  @Param("status") com.nelly.hivtbmonitoringsystem.enums.SyncStatus status);

    @Query("SELECT COUNT(tp) FROM TreatmentPlan tp WHERE tp.patient.facility.id = :facilityId AND tp.syncStatus = :status")
    long countByFacilityIdAndSyncStatus(@Param("facilityId") UUID facilityId,
                                         @Param("status") com.nelly.hivtbmonitoringsystem.enums.SyncStatus status);

    long countBySyncStatus(com.nelly.hivtbmonitoringsystem.enums.SyncStatus status);
}
