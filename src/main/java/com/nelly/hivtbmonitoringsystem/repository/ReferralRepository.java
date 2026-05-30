package com.nelly.hivtbmonitoringsystem.repository;

import com.nelly.hivtbmonitoringsystem.entity.Referral;
import com.nelly.hivtbmonitoringsystem.enums.ReferralStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReferralRepository extends JpaRepository<Referral, UUID> {
    List<Referral> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    List<Referral> findByReferredByChwIdOrderByCreatedAtDesc(UUID chwId);
    List<Referral> findByStatusOrderByCreatedAtDesc(ReferralStatus status);
    List<Referral> findByPatientFacilityIdOrderByCreatedAtDesc(UUID facilityId);
    List<Referral> findByPatientFacilityIdAndStatusOrderByCreatedAtDesc(UUID facilityId, ReferralStatus status);
}
