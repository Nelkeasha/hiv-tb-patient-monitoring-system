package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.TreatmentPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TreatmentPlanRepository extends JpaRepository<TreatmentPlan, UUID> {
    List<TreatmentPlan> findByPatientId(UUID patientId);
    Optional<TreatmentPlan> findByPatientIdAndIsActiveTrue(UUID patientId);
    Optional<TreatmentPlan> findByFhirCarePlanId(String fhirCarePlanId);
}
