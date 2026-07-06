package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.HomeVisitTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HomeVisitTaskRepository extends JpaRepository<HomeVisitTask, UUID> {

    boolean existsByPatientIdAndTriggerTypeAndStatus(UUID patientId, String triggerType, String status);

    List<HomeVisitTask> findByChwIdAndStatusOrderByCreatedAtDesc(UUID chwId, String status);

    List<HomeVisitTask> findByPatientIdAndStatus(UUID patientId, String status);

    List<HomeVisitTask> findByStatusOrderByCreatedAtDesc(String status);

    List<HomeVisitTask> findByChwFacilityIdAndStatusOrderByCreatedAtDesc(UUID facilityId, String status);
}
