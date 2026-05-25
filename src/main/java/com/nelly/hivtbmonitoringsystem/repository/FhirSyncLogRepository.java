package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.FhirSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FhirSyncLogRepository extends JpaRepository<FhirSyncLog, UUID> {
    List<FhirSyncLog> findByChwId(UUID chwId);
    List<FhirSyncLog> findByChwIdOrderBySyncStartedAtDesc(UUID chwId);
}
