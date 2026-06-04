package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.HomeVisit;
import com.nelly.hivtbmonitoringsystem.enums.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    long countByChwIdAndVisitDateAfter(UUID chwId, LocalDateTime after);
    List<HomeVisit> findByChwIdOrderByVisitDateDesc(UUID chwId);
    List<HomeVisit> findByChwIdAndSyncStatus(UUID chwId, SyncStatus syncStatus);

    @Query("SELECT COUNT(hv) FROM HomeVisit hv WHERE hv.chw.facility.id = :facilityId AND hv.syncStatus = :status")
    long countByFacilityIdAndSyncStatus(@Param("facilityId") UUID facilityId, @Param("status") SyncStatus status);

    /**
     * Find the most recent visit date per patient — used by LtfuScheduler to
     * detect patients with no care contact in 28+ days (monthly ART/TB refill cycle).
     */
    @Query("SELECT hv.patient.id FROM HomeVisit hv " +
           "GROUP BY hv.patient.id " +
           "HAVING MAX(hv.visitDate) < :cutoff")
    List<UUID> findPatientIdsWithNoVisitSince(@Param("cutoff") LocalDateTime cutoff);

    /** Count visits for an entire facility on a specific calendar day — used for weekly activity trend. */
    @Query("SELECT COUNT(hv) FROM HomeVisit hv " +
           "WHERE hv.chw.facility.id = :facilityId " +
           "AND hv.visitDate >= :dayStart AND hv.visitDate < :dayEnd")
    long countByFacilityAndDay(
            @Param("facilityId") UUID facilityId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);
}
