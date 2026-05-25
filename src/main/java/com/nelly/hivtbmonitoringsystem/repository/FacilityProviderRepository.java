package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.FacilityProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FacilityProviderRepository extends JpaRepository<FacilityProvider, UUID> {
    Optional<FacilityProvider> findByUserId(UUID userId);
    List<FacilityProvider> findByFacilityId(UUID facilityId);
}
