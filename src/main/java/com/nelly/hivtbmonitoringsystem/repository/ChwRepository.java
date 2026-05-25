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
}
