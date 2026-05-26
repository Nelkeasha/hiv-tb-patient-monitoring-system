package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.DoseSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DoseScheduleRepository extends JpaRepository<DoseSchedule, UUID> {
    List<DoseSchedule> findByPlanId(UUID planId);
    List<DoseSchedule> findByPatientIdAndIsActiveTrue(UUID patientId);
    List<DoseSchedule> findByPlanIdAndIsActiveTrue(UUID planId);
    List<DoseSchedule> findByIsActiveTrue();
}
