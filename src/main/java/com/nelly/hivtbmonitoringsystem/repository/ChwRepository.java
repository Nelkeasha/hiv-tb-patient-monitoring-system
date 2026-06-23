package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Chw;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChwRepository extends JpaRepository<Chw, UUID> {
    Optional<Chw> findByUserId(UUID userId);
    Optional<Chw> findByEmployeeCode(String employeeCode);
    List<Chw> findByFacilityId(UUID facilityId);
    List<Chw> findByIsActiveTrue();

    /**
     * Village-level match comes first when auto-assigning a self-presented patient to a CHW.
     * Returns all active matches (not just one) so the caller can tie-break by caseload
     * when more than one CHW covers the same village/sector.
     */
    List<Chw> findByIsActiveTrueAndAssignedVillageIgnoreCase(String assignedVillage);
    List<Chw> findByIsActiveTrueAndAssignedSectorIgnoreCase(String assignedSector);
}
