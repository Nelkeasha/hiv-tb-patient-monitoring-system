package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.HomeVisit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface HomeVisitRepository extends JpaRepository<HomeVisit, UUID> {
    List<HomeVisit> findByPatientId(UUID patientId);
    List<HomeVisit> findByChwId(UUID chwId);
    List<HomeVisit> findByPatientIdOrderByVisitDateDesc(UUID patientId);
    List<HomeVisit> findByChwIdAndVisitDateBetween(UUID chwId, LocalDateTime from, LocalDateTime to);
    long countByPatientIdAndVisitDateAfter(UUID patientId, LocalDateTime after);
}
