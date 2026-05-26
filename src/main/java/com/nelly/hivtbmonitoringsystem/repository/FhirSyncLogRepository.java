package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.FhirSyncLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FhirSyncLogRepository extends JpaRepository<FhirSyncLog, UUID> {
    List<FhirSyncLog> findByChwId(UUID chwId);
    List<FhirSyncLog> findByChwIdOrderBySyncStartedAtDesc(UUID chwId);

    @Query("SELECT MAX(l.syncCompletedAt) FROM FhirSyncLog l " +
           "WHERE l.chw.facility.id = :facilityId AND l.syncStatus = 'COMPLETED'")
    Optional<LocalDateTime> findLastCompletedSyncForFacility(@Param("facilityId") UUID facilityId);
}
