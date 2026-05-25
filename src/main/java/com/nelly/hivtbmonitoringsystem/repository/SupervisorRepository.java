package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupervisorRepository extends JpaRepository<Supervisor, UUID> {
    Optional<Supervisor> findByUserId(UUID userId);
    List<Supervisor> findByFacilityId(UUID facilityId);
    List<Supervisor> findByDistrict(String district);
}
